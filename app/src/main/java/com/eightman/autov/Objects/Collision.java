package com.eightman.autov.Objects;

import java.util.UUID;

/**
 * Created by gilzhaiek on 2016-11-10.
 */

public class Collision {
    private CollisionPosition collisionPositionActive;
    private CollisionPosition collisionPositionPassive;

    public Collision(CarPosition positionActive, UUID uuidActive,
                     CarPosition positionPassive, UUID uuidPassive) {
        this.collisionPositionActive = new CollisionPosition(uuidActive, positionActive);
        this.collisionPositionPassive = new CollisionPosition(uuidPassive, positionPassive);
    }

    public CollisionPosition getCollisionPositionActive() {
        return collisionPositionActive;
    }

    public CollisionPosition getCollisionPositionPassive() {
        return collisionPositionPassive;
    }

    public class CollisionPosition {
        UUID carUUID;
        CarPosition position;

        public CollisionPosition(UUID carUUID, CarPosition position) {
            this.carUUID = carUUID;
            this.position = position;
        }

        public UUID getCarUUID() {
            return carUUID;
        }

        public CarPosition getPosition() {
            return position;
        }
    }
}
