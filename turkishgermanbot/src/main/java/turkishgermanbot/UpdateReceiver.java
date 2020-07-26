package turkishgermanbot;

import org.telegram.telegrambots.meta.api.objects.Update;

public class UpdateReceiver implements Runnable {
	
	private Update update;
	private int id;
	
	public UpdateReceiver(Update update, int id) {
	
		//super();
		this.update = update;
		this.id = id;
	}

	public void run() {
		// TODO Auto-generated method stub
		System.out.println("DENEMEMSG" + update.getMessage().getText());
		System.out.println(update.toString());
	}
	 
	
	
	
}
