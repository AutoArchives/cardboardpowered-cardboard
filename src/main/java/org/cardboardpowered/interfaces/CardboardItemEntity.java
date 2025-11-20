package org.cardboardpowered.interfaces;

public interface CardboardItemEntity {

	int cardboard$getHealth();

	void cardboard$setHealth(int newVal);

	int cardboard$itemAge();

	void cardboard$setUnlimitedAge(boolean noLimit);

	void cardboard$setItemAge(int value);

}
