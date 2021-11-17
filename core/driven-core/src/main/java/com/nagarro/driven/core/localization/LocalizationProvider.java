package com.nagarro.driven.core.localization;


import com.github.pemistahl.lingua.api.Language;
import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;
import com.nagarro.driven.core.constant.FrameworkCoreConstant;
import com.nagarro.driven.core.localization.models.LocaleString;
import com.nagarro.driven.core.localization.models.LocalisedModules;
import com.nagarro.driven.core.weblocator.file.util.WebLocatorFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.nio.file.Path;
import java.util.Optional;


public class LocalizationProvider {

    private LocalizationProvider() {

    }

    private static final LanguageDetector detector = LanguageDetectorBuilder.fromAllLanguages().build();

    private static final Logger log = LoggerFactory.getLogger(LocalizationProvider.class);

    public static Language detectLanguage(String sampleString) {
        try {
            Language detectedLanguage = detector.detectLanguageOf(sampleString);
            log.info("The language of the sample string after detecting is:{}", detectedLanguage);
            return detectedLanguage;
        }catch (Exception e){
            log.info("The language is not detected");
            e.printStackTrace();
            return null;
        }
    }

    public static String translateString(String candidate, String targetLanguage) {
        Language source = detectLanguage(candidate);
        return translateString(candidate, source, targetLanguage);
    }

    public static String translateString(String candidate, Language sourceLanguage, String targetLanguage) {

        return Optional.ofNullable(sourceLanguage)
                .map(Language::toString)
                .map(LocalizationProvider::findFile)
                .map(LocalisedModules::getLocaleStrings)
                .flatMap(localeStrings->localeStrings.stream()
                        .filter(entry->entry.getLocaleString().equals(candidate))
                        .findFirst())
                .map(LocaleString::getName)
                .map(localeName->getLocalizedString(localeName,targetLanguage))
                .toString();
    }

    public static String getLocalizedString(String stringName, String targetLanguage){

        return Optional.ofNullable(targetLanguage)
                .map(LocalizationProvider::findFile)
                .map(LocalisedModules::getLocaleStrings)
                .flatMap(localeStrings -> localeStrings.stream()
                        .filter(entry -> entry.getName().equals(stringName))
                        .findFirst())
                .map(LocaleString::getLocaleString)
                .toString();
    }

    public static LocalisedModules findFile(String languageFileName) {

        Optional<String> filepath = Optional.of(new WebLocatorFiles(FrameworkCoreConstant.XML_FILE_EXTENSION))
                .map(WebLocatorFiles::getFiles)
                .map(files -> files.stream().filter(file -> file.getName().equals(languageFileName))
                        .findAny()).flatMap(module -> module.map(File::getAbsolutePath));

        if (filepath.isPresent()) {
            try {
                File xmlFile = Path.of(String.valueOf(filepath)).toFile();
                JAXBContext jc = JAXBContext.newInstance(LocalisedModules.class);
                Unmarshaller unmarshaller = jc.createUnmarshaller();
                return (LocalisedModules) unmarshaller.unmarshal(xmlFile);
            } catch (JAXBException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }
}
