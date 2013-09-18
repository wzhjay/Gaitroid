package com.gaitroid;

public class User {
	//private variables
    String _id;
    String _username;
    String _password;
    String _firstname;
    String _lastname;
    String _gender;
    String _email;
    String _phone;
    String _age;
    String _country;
    String _city;
    String _street;
    String _postcode;
    String _createTime;
    
    // Empty constructor
    public User(){
    }
    
 // constructor
    public User(String id,
    		String _username,
    		String _password,
    		String _firstname,
    		String _lastname,
    		String _gender,
    		String _email,
    		String _phone,
    		String _age,
    		String _country,
    		String _city,
    		String _street,
    		String _postcode,
    		String _createTime){
        this._id = id;
        this._username = _username;
        this._password = _password;
        this._firstname = _firstname;
        this._lastname = _lastname;
        this._gender = _gender;
        this._email = _email;
        this._phone = _phone;
        this._age = _age;
        this._country = _country;
        this._city = _city;
        this._street = _street;
        this._postcode = _postcode;
        this._createTime = _createTime;
    }
    
    public String getID(){
        return this._id;
    }
    
    public String getUsername(){
        return this._username;
    }
    
    public void setUsername(String username){
    	this._username = username;
    }
    
    public String getPassword(){
        return this._password;
    }
    
    public String getFirstname(){
        return this._firstname;
    }
    
    public void setFirstname(String firstname){
        this._firstname = firstname;
    }
    
    public String getLastname(){
        return this._lastname;
    }
    
    public void setLastname(String lastname){
        this._lastname = lastname;
    }
    
    public String getGender(){
        return this._gender;
    }
    
    public void setGender(String gender){
    	this._gender = gender;
    }
    
    public String getEmail(){
        return this._email;
    }
    
    public void setEmail(String email){
        this._email = email;
    }
    
    public String getPhone(){
        return this._phone;
    }
    
    public void setPhone(String phone){
    	this._phone = phone;
    }
    
    public String getAge(){
        return this._age;
    }
    
    public void setAge(String age){
    	this._age = age;
    }
    
    public String getCountry(){
        return this._country;
    }
    
    public void setCountry(String country){
    	this._country = country;
    }
    
    public String getCity(){
        return this._city;
    }
    
    public void setCity(String city){
        this._city = city;
    }
    
    public String getStreet(){
        return this._street;
    }
    
    public void setStreet(String street){
    	this._street = street;
    }
    
    public String getPostcode(){
        return this._postcode;
    }
    
    public void setPostcode(String postcode){
    	this._postcode = postcode;
    }
    
    public String getCreateTime(){
    	return this._createTime;
    }
}
