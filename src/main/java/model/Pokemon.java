package model;

import java.util.List;

public class Pokemon {
    private int id;
    private String name;
    private Type type;
    private int maxHp;
    private int currentHp;
    private int attack;
    private int defence;
    private int speed;
    private String sprite;
    private List<Move> move;

    public Pokemon(
            int id, String name, Type type,
            int maxHp, int currentHp,
            int attack, int defence, int speed,
            List<Move> move, String sprite){
        this.id = id;
        this.name = name;
        this.type = type;
        this.maxHp = maxHp;
        this.currentHp = currentHp;
        this.attack = attack;
        this.defence = defence;
        this.speed = speed;
        this.sprite = sprite;
        this.move = move;
    }

    //--------------Getters y Setters----------------------------
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
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

    public List<Move> getMove() {
        return move;
    }

    public void setMove(List<Move> move) {
        this.move = move;
    }

    //-------Metodos---------

    public void takeDamage(int damage){
        this.currentHp -= damage;
        if(this.currentHp < 0){
            this.currentHp = 0;
        }
    }

    public boolean isFanted(){
        return this.currentHp == 0;
    }
}
