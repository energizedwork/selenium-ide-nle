package uk.co.acuminous.util

import java.text.Normalizer.Form

class Normalizer {

    private static Map<String, String> EXPLICIT_REPLACEMENTS = [
        "\u00f8": 'o',  "\u00d8": 'O',
        "\u00fe": 'th', "\u00de": 'Th',
        "\u00e6": 'ae', "\u00c6": 'AE',
        "\u00F0": 'dh', "\u00d0": 'Dh',
        "\u00DF": 'sz'    ]

    public static String normalize(String src) {
    	if (src != null) {

            src = safeDecode(src)
            src = java.text.Normalizer.normalize(src, Form.NFD)
            EXPLICIT_REPLACEMENTS.each { String k, String v -> src = src.replaceAll(k, v) }
            src = src.replaceAll(/\p{InCombiningDiacriticalMarks}+/, '').toLowerCase()

            src = src.replaceAll(/&/, '-and-')
            src = src.replaceAll(/[^\s\w\d-_]+/, '') // Remove completely unwanted characters
            src = src.replaceAll(/[\s-_]+/, '-') // Replace whitepace / hyphens / underscores with a single hyphen
            src = src.replaceAll(/^[-]+/, '') // Remove leading hyphens
            src = src.replaceAll(/[-]+$/, '') // Remove trailing hyphens
    	}
    	return src
    }

    private static String safeDecode(String src) {
        try {
             src = URLDecoder.decode(src, 'utf8')
        } catch (Exception e) {
            // URL May have an invalid hexadecimal escape squences such as %ZZ
        }
        return src
    }
}