package uk.co.acuminous.test

import uk.co.acuminous.books.jbehave.converter.CustomConvertersLoader
import org.jbehave.core.steps.StepFinder.ByLevenshteinDistance
import org.jbehave.core.steps.StepFinder.PrioritisingStrategy
import org.jbehave.core.steps.StepFinder

import org.jbehave.core.steps.StepCandidate
import org.jbehave.core.steps.Step
import uk.co.acuminous.books.jbehave.step.TestFixtures
import org.jbehave.core.steps.InstanceStepsFactory
import org.jbehave.core.configuration.MostUsefulConfiguration
import org.jbehave.core.configuration.Configuration
import org.jbehave.core.steps.InjectableStepsFactory
import javax.servlet.http.HttpServletResponse

import grails.converters.JSON
import uk.co.acuminous.books.Invoice
import uk.co.acuminous.books.Expense
import uk.co.acuminous.books.VatRate
import uk.co.acuminous.books.VatReturn
import org.jbehave.core.steps.StepResult
import uk.co.acuminous.books.TaxYear
import uk.co.acuminous.books.bootstrap.TaxYearBootStrapper
import uk.co.acuminous.books.bootstrap.VatRateBootStrapper
import javax.sql.DataSource
import groovy.sql.Sql
import org.hibernate.SessionFactory
import uk.co.acuminous.books.builder.InvoiceBuilder
import uk.co.acuminous.books.builder.AmountBuilder
import uk.co.acuminous.books.Amount
import org.joda.time.LocalDate

class FixtureController {

    Map testContext = [:]
    List fixtures = [new TestFixtures(testContext)]
    List candidateSteps
    StepFinder stepFinder
    DataSource dataSource
    SessionFactory sessionFactory

    public FixtureController() {
        
        Configuration configuration = new MostUsefulConfiguration()
                .useParameterConverters(CustomConvertersLoader.getConverters())

        InjectableStepsFactory stepsFactory = new InstanceStepsFactory(configuration, fixtures)
        candidateSteps = stepsFactory.createCandidateSteps();

        PrioritisingStrategy prioritisingStrategy = new ByLevenshteinDistance()
        stepFinder = new StepFinder(prioritisingStrategy)
    }

    def setupInvoice = { InvoiceCmd cmd ->
        Amount amount = new AmountBuilder()
            .net(cmd.net)
            .vatRate(cmd.vatRate)
            .build()

        Invoice invoice = new InvoiceBuilder()
            .reference(cmd.reference)
            .amount(amount)
            .raised(cmd.raised)
            .settled(cmd.settled)
            .buildAndSave()

        render("<div id='invoice'><div id='id'>${invoice.id}</div><div id='ref'>${invoice.reference}</div>")
    }
    
	def index = {
        List<StepCandidate> candidates = stepFinder.collectCandidates(candidateSteps)
        render(view:'index', model:[candidates:candidates])
	}

    def setup = {
        session.invalidate()

        sessionFactory.currentSession.clear()
        
	Sql sql = new Sql(dataSource)
        [Invoice, Expense, VatReturn, TaxYear].collect { Class clazz ->
            String tableName = sessionFactory.getClassMetadata(clazz).tableName
            sql.execute("delete from ${tableName}".toString())
	}
	sql.close()

        VatRateBootStrapper.run()
        TaxYearBootStrapper.run()
        
        render "OK"
    }

	def perform = {
        Step step = getMostLikelyStep(params.step)
        if (step) {
            StepResult stepResult = step.perform()
            if (stepResult.failure) {
                throw stepResult.failure
            }

            if (request.getHeader('view') == 'json') {
                render(testContext.result as JSON)
            } else if (request.getHeader('view') == 'none') {
                render ""
            } else {
                Map result = [:]
                result[testContext.result.class.simpleName] = testContext.result
                render((result as JSON).toString())
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No matching step for '${params.step}'")
        }
	}

    private Step getMostLikelyStep(String stepAsText) {
        List<StepCandidate> stepCandidates = stepFinder.collectCandidates(candidateSteps)
        StepCandidate bestCandidate = stepFinder.prioritise(stepAsText, stepCandidates).find { StepCandidate candidate ->
            candidate.matches(stepAsText)
        }
        bestCandidate ? bestCandidate.createMatchedStep(stepAsText, [:]) : null
    }

    def resolveMessage = {
        List args = []
        if (params.args) {
            List strArgs = params['args'].class.isArray() ? params.args : [params.args]
            strArgs.each { String value ->
                if (value ==~ /[0-9]+/) {
                        args << new Integer(value)
                } else {
                        args << value
                }
            }
        }
        String result = message(code: params.code, args:args, default:"Missing message code: ${params.code}")
        render ([msg: result] as JSON)
    }

    def sleep = {
        Long millis = params.millis ? Long.parseLong(params.millis) : 1000L
        Thread.sleep(millis)
        render "OK"
    }
}

class InvoiceCmd {

    LocalDate raised
    LocalDate settled
    VatRate vatRate
    BigDecimal vat
    BigDecimal net
    String reference

    VatRate getVatRate() {
        return VatRate.findByChargeable(vat)
    }
}
