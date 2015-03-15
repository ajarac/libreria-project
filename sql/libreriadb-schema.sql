drop database if exists libreriadb;
create database libreriadb;

use libreriadb;

create table users (
	username	varchar(20) not null primary key,
	userpass	char(32) not null,
	name		varchar(70) not null
);

create table user_roles (
	username			varchar(20) not null,
	rolename 			varchar(20) not null,
	foreign key(username) references users(username) on delete cascade,
	primary key (username, rolename)
);

create table authors (
	authorid			int not null auto_increment primary key,
	name				varchar(80) not null
);

create table books (
	bookid				int not null auto_increment primary key,
	title	 			varchar(100) not null,
	language			varchar(100) not null,
	edition				varchar(100) not null,
	editionDate			varchar(100) not null,
	printingDate			varchar(100) not null,
	publisher			varchar(100) not null,
	last_modified			timestamp default current_timestamp ON UPDATE CURRENT_TIMESTAMP,
	creation_timestamp		datetime not null default current_timestamp
);

create table authors_books (
	authorid			int not null,
	bookid				int not null,
	foreign key (authorid) references authors(authorid) on delete cascade,
	foreign key (bookid) references books(bookid) on delete cascade,
	primary key (authorid, bookid)
);

create table reviews (
	reviewid 			int not null auto_increment,
	username 			varchar(20) not null,
	name				varchar(70) not null,
	bookid				int not null,
	content				varchar(500) not null,
	last_modified			timestamp default current_timestamp ON UPDATE CURRENT_TIMESTAMP,
	creation_timestamp		datetime not null default current_timestamp,
	foreign key(username) 		references users(username) on delete cascade,
	foreign key(bookid)		references books(bookid) on delete cascade,
	primary key (reviewid, username, bookid)
);
