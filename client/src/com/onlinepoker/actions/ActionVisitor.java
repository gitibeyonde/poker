package com.onlinepoker.actions;


public interface ActionVisitor {


	void handleDefaultAction     (Action action);

	void handleBettingAction     (Action action);

	void handleSimpleAction      (SimpleAction action);

	void handleStageAction       (StageAction action);
	
	void handleCardAction        (CardAction  action);

	void handleTableServerAction (TableServerAction action);
	
	void handleErrorAction       (ErrorAction action);
}
