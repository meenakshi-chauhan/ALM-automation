package com.nagarro.driven.runner.testng.base.listener;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.nagarro.driven.runner.testng.base.TestCaseBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.xml.XmlClass;

import java.util.List;
import java.util.Objects;

import static com.google.inject.Guice.createInjector;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * An abstract class of all TestNG listeners which provides a generic dependency injection logic
 */

public abstract class AbstractTestNgListener implements ITestListener {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractTestNgListener.class);

    @Override
    public void onStart(ITestContext context) {
        // Parent injector contains all loaded test class modules as bindings, so first of all they
        // should be retrieved
        final Injector parentInjector = context.getSuite().getParentInjector();
        List<Module> parentInjectorTestClassModules =
                parentInjector.getAllBindings().values().stream()
                        .map(Binding::getProvider)
                        .map(Provider::get)
                        .filter(module -> module instanceof TestCaseBase.TestCaseBaseModule)
                        .map(module -> (TestCaseBase.TestCaseBaseModule) module)
                        .collect(toList());
        if (parentInjectorTestClassModules.isEmpty()) {
            String invokingTestClassesList =
                    context.getCurrentXmlTest().getClasses().stream()
                            .map(XmlClass::getName)
                            .collect(joining(", "));
            LOG.error(
                    "Couldn't inject the needed dependencies into {} listener because TestNG context has no "
                            + "registered test case modules. Please check if the invoking test class(es) <{}> is(are) correctly "
                            + "configured in order to use dependency injection",
                    this.getClass().getCanonicalName(),
                    invokingTestClassesList);
        } else {
            // Currently invoking test class modules which are registered by TestNG in the list of
            // injectors are not the same as the bindings of the parent injector (seems like a bug).
            // That's why in case there's no explicit existing injector, already registered guice modules
            // map must be checked before creating a new injector
            Injector testListenersInjector =
                    ofNullable(context.getInjector(parentInjectorTestClassModules))
                            .orElseGet(
                                    () ->
                                            parentInjectorTestClassModules.stream()
                                                    .map(module -> context.getGuiceModules(module.getClass()))
                                                    .filter(Objects::nonNull)
                                                    .filter(injectorModules -> !injectorModules.isEmpty())
                                                    .map(context::getInjector)
                                                    .filter(Objects::nonNull)
                                                    .findAny()
                                                    .orElseGet(
                                                            () -> {
                                                                Injector newInjector =
                                                                        createInjector(parentInjectorTestClassModules);
                                                                context.addInjector(parentInjectorTestClassModules, newInjector);
                                                                return newInjector;
                                                            }));
            testListenersInjector.injectMembers(this);
        }
    }
}
