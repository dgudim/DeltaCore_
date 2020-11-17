package com.deo.flapd.model.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class Entity {

    float x;
    float y;
    float width;
    float height;
    float offsetX = 0;
    float offsetY = 0;
    float originX;
    float originY;
    float rotation = 0;
    float health;
    boolean isDead = false;
    boolean canAimAt = false;
    Color color;

    Sprite entitySprite;
    Rectangle entityHitBox;

    public void init(){
        entitySprite.setSize(width, height);
        entitySprite.setOrigin(originX, originY);
        entitySprite.setPosition(x, y);
        entityHitBox = entitySprite.getBoundingRectangle();
        color = Color.WHITE;
    }

    public void update(){
        entitySprite.setPosition(x, y);
        entitySprite.setRotation(rotation);
        entitySprite.setColor(color);
    }

}