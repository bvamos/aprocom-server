package com.aprohirdetes.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("session")
public class Session {

	@Id private ObjectId id;
	
	private String felhasznaloNev;
	private long kezdet;
	private long lejarat;
	private long utolsoKeres;
	
}