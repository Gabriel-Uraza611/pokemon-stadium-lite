package model;

import java.awt.*;
import java.util.List;

public class Pokemon {
    private int id;
    private String name;
    private List<Type> types;
    private int maxHp;
    private int currentHp;
    private int attack;
    private int defence;
    private int speed;
    private String sprite;
    private transient Image spriteImage;
    private List<Move> moves;

    public Pokemon(
            int id, String name, List<Type> types,
            int maxHp, int currentHp,
            int attack, int defence, int speed,
            List<Move> moves, String sprite){
        this.id = id;
        this.name = name;
        this.types = types;
        this.maxHp = maxHp;
        this.currentHp = currentHp;
        this.attack = attack;
        this.defence = defence;
        this.speed = speed;
        this.sprite = sprite;
        this.moves = moves;
    }

    public Pokemon(){}

    //--------------Getters y Setters----------------------------

    public Image getSpriteImage() {
        return spriteImage;
    }

    public void setSpriteImage(Image spriteImage) {
        this.spriteImage = spriteImage;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public void setMoves(List<Move> moves) {
        this.moves = moves;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Type> getTypes() {
        return types;
    }

    public void setTypes(List<Type> types) {
        this.types = types;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefence() {
        return defence;
    }

    public void setDefence(int defence) {
        this.defence = defence;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getSprite() {
        return sprite;
    }

    public void setSprite(String sprite) {
        this.sprite = sprite;
    }

    //-------Metodos---------

    public void takeDamage(int damage){
        this.currentHp -= damage;
        if(this.currentHp < 0){
            this.currentHp = 0;
        }
    }

    public boolean isFainted(){
        return this.currentHp == 0;
    }
}
