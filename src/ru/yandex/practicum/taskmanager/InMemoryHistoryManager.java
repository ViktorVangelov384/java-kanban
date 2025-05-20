package ru.yandex.practicum.taskmanager;

import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.Subtask;
import ru.yandex.practicum.task.Task;

import java.util.*;
import java.util.ArrayList;


public class InMemoryHistoryManager implements HistoryManager {
    private static class Node {
        Task task;
        Node prev;
        Node next;

        Node(Task task) {
            this.task = task;
        }
    }

    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {
        if (task == null) return;
        remove(task.getId());
        Node newNode = new Node(createCopy(task));
        linkLast(newNode);
        nodeMap.put(task.getId(), newNode);
    }

    private Task createCopy(Task original) {
        if (original == null) {
            return null;
        }
        if (original instanceof Epic epic) {
            Epic copy = new Epic(epic.getName(), epic.getDescription());
            copy.setId(epic.getId());
            copy.setStatus(epic.getStatus());
            return copy;
        } else if (original instanceof Subtask subtask) {
            Subtask copy = new Subtask(
                    subtask.getName(),
                    subtask.getDescription(),
                    subtask.getStatus(),
                    subtask.getEpicId()
            );
            copy.setId(subtask.getId());
            return copy;
        } else {
            Task copy = new Task(
                    original.getName(),
                    original.getDescription(),
                    original.getStatus()
            );
            copy.setId(original.getId());
            return copy;
        }
    }

    @Override
    public void remove(int id) {
        Node node = nodeMap.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTask();
    }

    private List<Task> getTask() {
        List<Task> tasks = new ArrayList<>();
        Node current = head;
        while (current != null) {
            tasks.add(current.task);
            current = current.next;
        }
        return tasks;
    }

    private void linkLast(Node node) {
        if (head == null) {
            head = node;
        } else {
            tail.next = node;
            node.prev = tail;
        }
        tail = node;
    }

    private void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;

        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }
}
