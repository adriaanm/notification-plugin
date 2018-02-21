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

import javax.inject.Inject;

/**
 * Workflow step to send a webhook notification. For use in pipeline jobs where the job end notification sometimes doesn't get sent properly (no idea why).
 */
public class NotificationSendStep extends AbstractStepImpl {
    private final boolean done;
    public boolean isDone() { return done; }

    @DataBoundConstructor
    public NotificationSendStep(boolean done) {
        this.done = done;
    }

    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {
        public DescriptorImpl() { super(NotificationSendStepExecution.class); }
        @Override public String getFunctionName() { return "notificationSend"; }
        @Override public String getDisplayName() { return "Send Job Notification"; }
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
            listener.getLogger().println("Notification: "+ (step.done ? "done" : "working" ));
            listener.getLogger().println("(Info from run: building= "+ run.isBuilding() +" "+
                    run.getBuildStatusSummary().message +
                    " duration= "+ run.getDuration() +")");

            Phase phase = step.done ? Phase.COMPLETED : Phase.STARTED;
            phase.handle(run, listener, run.getTimeInMillis() + run.getDuration());

            return null;
        }
    }

}
