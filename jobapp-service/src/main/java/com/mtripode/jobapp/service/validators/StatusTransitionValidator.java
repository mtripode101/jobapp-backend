package com.mtripode.jobapp.service.validators;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

import com.mtripode.jobapp.service.model.Status;

public class StatusTransitionValidator {

    private static final EnumMap<Status, Set<Status>> validTransitions = new EnumMap<>(Status.class);

    static {
        validTransitions.put(Status.APPLIED, EnumSet.of(Status.INTERVIEW_SCHEDULED, Status.REJECTED, Status.APPLIED));
        validTransitions.put(Status.INTERVIEW_SCHEDULED, EnumSet.of(Status.INTERVIEWED, Status.REJECTED, Status.INTERVIEW_SCHEDULED));
        validTransitions.put(Status.INTERVIEWED, EnumSet.of(Status.OFFERED, Status.REJECTED, Status.INTERVIEWED));
        validTransitions.put(Status.OFFERED, EnumSet.of(Status.HIRED, Status.REJECTED,Status.OFFERED));
        validTransitions.put(Status.HIRED, EnumSet.noneOf(Status.class));    
        validTransitions.put(Status.REJECTED, EnumSet.of(Status.REJECTED)); 
    }

    public static boolean canTransition(Status from, Status to) {
        return validTransitions.getOrDefault(from, EnumSet.noneOf(Status.class)).contains(to);
    }
}
