package com.eightman.autov.Objects;

import com.eightman.autov.Utils.CollisionUtils;

import java.util.UUID;

/**
 * Created by gilzhaiek on 2016-11-10.
 */

public class Collision {
    final private CollisionPosition collisionPositionActive;
    final private CollisionPosition collisionPositionPassive;
    final private CollisionUtils.Side side;

    public Collision(CarPosition positionActive, UUID uuidActive,
                     CarPosition positionPassive, UUID uuidPassive,
                     CollisionUtils.Side side) {
        this.collisionPositionActive = new CollisionPosition(uuidActive, positionActive);
        this.collisionPositionPassive = new CollisionPosition(uuidPassive, positionPassive);
        this.side = side;
    }

    public CollisionPosition getCollisionPositionActive() {
        return collisionPositionActive;
    }

    public CollisionPosition getCollisionPositionPassive() {
        return collisionPositionPassive;
    }

    public CollisionUtils.Side getSide() {
        return side;
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
