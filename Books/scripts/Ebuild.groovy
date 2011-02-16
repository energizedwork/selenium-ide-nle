scriptEnv = 'production'

includeTargets << grailsScript("Init")
includeTargets << grailsScript("War")


target(main: "Creates a gentoo ebuild") {
    depends( war )

    String ebuildFilename = "${grailsAppName}-${grailsAppVersion}.ebuild".toLowerCase()
    String ebuildDir = "$basedir/target/ebuild"
    String packageDir = "$ebuildDir/acuminous/books"
    String filesDir = "$packageDir/files"
    String configDir = "$filesDir/config"

    ant.delete(dir: ebuildDir)

    ant.mkdir(dir: ebuildDir)
    ant.mkdir(dir: packageDir)
    ant.mkdir(dir: filesDir)
    ant.mkdir(dir: configDir)

    ant.copy(todir: filesDir, file: warName)
    ant.copy(todir: configDir) {
        fileset(dir: "$basedir/environment", includes: "*.groovy")
    }

    ant.copy(file: "$basedir/src/portage/books.ebuild", tofile: "$packageDir/$ebuildFilename")
    ant.copy(file: "$basedir/src/portage/metadata.xml", todir: packageDir)

    ant.exec(executable: 'tar') {
        arg(line: "--create --verbose --gzip --directory $ebuildDir --file $basedir/target/books-ebuild.tar.gz .")
    }
}

setDefaultTarget(main)
