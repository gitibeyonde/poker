package com.onlinepoker.actions;

import com.golconda.game.util.Card;


public class CardAction extends Action {

	private Card[] cards;
        private int cnt=0;

	public CardAction(int id, int target, Card[] cards) {
		super(id, ACTION_TYPE_CARD, target);
		this.cards = cards;
	}
        
        public CardAction(int id, int target, int size) {
                super(id, ACTION_TYPE_CARD, target);
                this.cnt = size;
        }
	public Card[] getCards() { return cards; }
        public int getCardsCount() { return cnt; }

	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(super.toString()).append(getTarget()).append(" ");
		if (cards != null) {
			for (int i = 0; i < cards.length; i++) {
				s.append(cards[i]);
				if (i != cards.length - 1) s.append(",");
			}
		}
		return s.toString();
	}

	public void handleAction(ActionVisitor v) {
		v.handleCardAction(this);
	}
}
