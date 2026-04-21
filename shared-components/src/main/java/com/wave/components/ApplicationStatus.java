package com.wave.components;

import com.wave.components.singly_linked_list.Node;

public enum ApplicationStatus {
    SUBMITTED,
    UNDER_REVIEW,
    INTERVIEW_SCHEDULED,
    OFFERED,
    REJECTED;

    private static final Node PIPELINE_HEAD;

    static {
        PIPELINE_HEAD = new Node(ApplicationStatus.SUBMITTED,
            new Node(ApplicationStatus.UNDER_REVIEW,
                    new Node(ApplicationStatus.INTERVIEW_SCHEDULED,
                            new Node(ApplicationStatus.OFFERED, null))));
    }

    public static Node findNode(ApplicationStatus status) {
        Node curr = PIPELINE_HEAD;
        while (curr != null) {
            if (curr.getData() == status) return curr;
            curr = curr.getNext();
        }
        return null;
    }
}