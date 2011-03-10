/*
 * Copyright 2010 Acuminous Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

naturalLanguageExtensions

    .addLocator('the home page', '/')

    .addLocator('the tax year selector', 'taxYearPicker')

    .addLocator('the $tabName tab', '//a[@title="$tabName"]')

    .addLocator('the $nth column in the $tableId table', function() {

        var nth = parseInt(this.inlineArguments.nth);
        var tableId = this.inlineArguments.tableId;

        return "//table[@id='" + tableId + "Table']/thead/tr/th[" + nth + "]"; 

    });


var parseNth = function(value) {
    return parseInt(value.substr(0, value.length - 2)) - 1;
};