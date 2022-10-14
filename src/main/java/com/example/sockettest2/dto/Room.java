package com.example.sockettest2.dto;

import lombok.Builder;
import lombok.Data;

import java.util.LinkedList;
import java.util.Objects;

@Data
@Builder
public class Room {
    private String roomNumber;
    private String roomName;
    private LinkedList<String> users;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Room other = (Room) obj;
        return Objects.equals(roomNumber, other.roomNumber);
    }
    @Override
    public int hashCode() {
        return Objects.hash(roomNumber);
    }
}

