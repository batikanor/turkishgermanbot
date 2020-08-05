package objects;

public class AdayFormResults {
public String timeStamp;
public String field; ///< Alan yani... mf tm falan... turkce yazmiyom nedense ;/
public String departmentChoice;
public String otherChoices;
public String ranking;
public String specialQuota;
public String Unlikeliness; // uste yazilan baska bi yere girme olasiligi
public String reason;

public AdayFormResults(String timeStamp, String field, String departmentChoice, String otherChoices, String ranking,
		String specialQuota, String unlikeliness, String reason) {
	super();
	this.timeStamp = timeStamp;
	this.field = field;
	this.departmentChoice = departmentChoice;
	this.otherChoices = otherChoices;
	this.ranking = ranking;
	this.specialQuota = specialQuota;
	Unlikeliness = unlikeliness;
	this.reason = reason;
}




}
