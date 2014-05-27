package edu.upc.eetac.dsa.dsaqp1314g5.gvent.api.model;

public class GventError {
	private int status;
	private String message;

	public GventError() {
		super();
	}

	public GventError(int status, String message) {
		super();
		this.status = status;
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}