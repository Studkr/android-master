package com.INT.apps.GpsspecialDevelopment.data.models.json_models.bonuses;

import com.google.gson.annotations.SerializedName;

public class BonusInfo{

	@SerializedName("money")
	private double money;

	@SerializedName("bonuses_per_money")
	private int bonusesPerMoney;

	@SerializedName("money_per_bonuses")
	private double moneyPerBonuses;

	@SerializedName("bonuses")
	private int bonuses;

	public void setMoney(int money){
		this.money = money;
	}

	public double getMoney(){
		return money;
	}

	public void setBonusesPerMoney(int bonusesPerMoney){
		this.bonusesPerMoney = bonusesPerMoney;
	}

	public int getBonusesPerMoney(){
		return bonusesPerMoney;
	}

	public void setMoneyPerBonuses(int moneyPerBonuses){
		this.moneyPerBonuses = moneyPerBonuses;
	}

	public double getMoneyPerBonuses(){
		return moneyPerBonuses;
	}

	public void setBonuses(int bonuses){
		this.bonuses = bonuses;
	}

	public int getBonuses(){
		return bonuses;
	}

	@Override
 	public String toString(){
		return 
			"BonusInfo{" + 
			"money = '" + money + '\'' + 
			",bonuses_per_money = '" + bonusesPerMoney + '\'' + 
			",money_per_bonuses = '" + moneyPerBonuses + '\'' + 
			",bonuses = '" + bonuses + '\'' + 
			"}";
		}
}