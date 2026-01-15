/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author Kuri
 */
public class PokeCard {
    private String id;//Unique identifier
    private String name;//Card name
    private String type;//Type(combo box)
    private String rarity;//Rarity(combo box)
    private String condition;//Condition(combo box)
    private double value;//Value
    private String imagePath; // Path to image

    /**
     * Constructor to create a new Pokémon card.
     * @param id Unique ID of the card.
     * @param name Name of the card.
     * @param type Type of the card.
     * @param rarity Rarity level of the card.
     * @param condition Condition of the card.
     * @param value Monetary value of the card.
     * @param imagePath Path to the cards image.
     */
    public PokeCard(String id, String name, String type, String rarity, String condition, double value, String imagePath) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.rarity = rarity;
        this.condition = condition;
        this.value = value;
        this.imagePath = imagePath;
    }

    // Getters and Setters for all fields (encapsulation)

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
    
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Overrides toString for easy display in JTable or debugging.
     * @return String representation of the card.
     */
    @Override
    public String toString() {
        return "PokemonCard{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", rarity='" + rarity + '\'' +
                ", condition='" + condition + '\'' +
                ", value=" + value +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
