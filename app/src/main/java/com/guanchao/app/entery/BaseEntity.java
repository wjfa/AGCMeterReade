package com.guanchao.app.entery;


public class BaseEntity<T> {

	/**
	 * data : {"fileId":"78","fileName":"1497602969142.png","filePath":"http://218.92.200.222:8081/imageServ/2017\\06\\16\\fe58cc9e-e8e6-4180-9491-0891f2a54a27.png"}
	 * message : 上传文件成功
	 * success : true
	 */

	/**
	 * 返回文字内容
	 */
	private String message;
	/**
	 * 状态
	 */
	private boolean success;
	/**
	 * 数据(可能是集合 也可能是对象)
	 */
	private T data;
	
	public BaseEntity() {
		// TODO Auto-generated constructor stub
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean getSuccess() {
		return success;
	}
	public void setSuccess(String status) {
		this.success = success;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
}
