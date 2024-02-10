/*
 * Created by Naimish Trivedi on 09/02/24, 5:49 pm
 * Copyright (c) 2024 . All rights reserved.
 * Last modified 09/02/24, 5:29 pm
 */

package naimishtrivedi.in.googleadsmanager;

public class RewardEarned {
    private int amount;
    private String type = "";

    public int getAmount() {
        return amount;
    }

    protected void setAmount(int amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    protected void setType(String type) {
        this.type = type;
    }
}
