package de.skuzzle.inject.async.schedule;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

class SchedulerTypeListener implements TypeListener {

    private static final Logger LOG = LoggerFactory.getLogger(SchedulerTypeListener.class);

    // synchronized in case the injector is set up asynchronously
    private List<Class<?>> scheduleStatics = Collections.synchronizedList(new ArrayList<>());
    private volatile boolean injectorReady;

    private final SchedulingService schedulingService;

    SchedulerTypeListener(SchedulingService schedulingService) {
        this.schedulingService = schedulingService;
    }

    @Inject
    void injectorReady() {
        this.injectorReady = true;
        final Consumer<Method> schedule = this.schedulingService::scheduleStaticMethod;

        this.scheduleStatics.forEach(type -> MethodVisitor.forEachStaticMethod(type, schedule));
        this.scheduleStatics.clear();
        this.scheduleStatics = null;
    }

    @Override
    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
        handleStaticScheduling(type.getRawType());
        handleMemberScheduling(encounter);
    }

    private void handleStaticScheduling(Class<?> type) {
        // need to distinguish two states here: the types we encounter while the injector
        // is not ready and those that are encountered while the injector is already
        // ready. In the first case, we collect the types for later handling in second
        // case we can schedule them immediately
        if (this.injectorReady) {
            LOG.trace("Encountered type {} while Injector was ready", type);
            MethodVisitor.forEachStaticMethod(type, this.schedulingService::scheduleStaticMethod);
        } else {
            LOG.trace("Encountered type {} while Injector was NOT ready", type);
            this.scheduleStatics.add(type);
        }
    }

    private <I> void handleMemberScheduling(TypeEncounter<I> encounter) {
        encounter.register(new InjectionListener<I>() {

            @Override
            public void afterInjection(I injectee) {
                final Consumer<Method> scheduleMemberMethod = method -> schedulingService.scheduleMemberMethod(method,
                        injectee);
                MethodVisitor.forEachMemberMethod(injectee.getClass(), scheduleMemberMethod);
            }
        });
    }

}
