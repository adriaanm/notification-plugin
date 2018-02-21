package com.tikal.hudson.plugins.notification.workflow;

import com.tikal.hudson.plugins.notification.Phase;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * Workflow step to send a webhook notification.
 */
public class NotificationSendStep extends AbstractStepImpl {
    private static final Logger logger = Logger.getLogger(NotificationSendStep.class.getName());

    @DataBoundConstructor
    public NotificationSendStep() {
    }

    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(NotificationSendStepExecution.class);
        }

        @Override
        public String getFunctionName() {
            return "notificationSend";
        }

        @Override
        public String getDisplayName() {
            return "Send Job Notification";
        }
    }

    public static class NotificationSendStepExecution extends AbstractSynchronousNonBlockingStepExecution<Void> {

        private static final long serialVersionUID = 1L;

        @Inject
        private transient NotificationSendStep step;

        @StepContextParameter
        private transient Run<?, ?> run;
        @StepContextParameter
        private transient TaskListener listener;

        @Override
        protected Void run() throws Exception {
            logger.info("Notification for "+ run.getFullDisplayName() +": "+ run.getBuildStatusSummary().message +" building= "+ run.isBuilding());
            Phase phase = run.isBuilding() ? Phase.STARTED : Phase.COMPLETED;

            phase.handle(run, listener, run.getTimeInMillis() + run.getDuration());

            return null;
        }

    }

}
