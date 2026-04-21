package com.wave.components.singly_linked_list;

import com.wave.components.ApplicationStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Node {
    private ApplicationStatus data;
    private Node next;
    public Node(ApplicationStatus data) { this.data = data; }
}
