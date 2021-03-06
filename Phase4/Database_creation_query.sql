use master
go

create table product(
	prod_id int primary key,
	prod_name varchar(100),
	prod_desc varchar(255),
	manu varchar(50),
	price float,
	unique( prod_name, prod_desc, manu ));

create table inventory(
	prod_id int primary key,
	inv_bulk int not null,
	inv_shelf int not null,
	foreign key (prod_id) references product,
	check (not inv_bulk < 0),
	check (not inv_shelf < 0));
	
create table master_account (
	acc_id int primary key,
	acc_type bit not null,
	password varchar(30) not null);
	
create table emplyee_account (
	acc_id int primary key,
	job varchar(30),
	productivity int not null,
	foreign key(acc_id) references master_account);
	
create table customer_account (
	acc_id int primary key,
	acc_address varchar(255) not null,
	email varchar(50) not null,
	foreign key(acc_id) references master_account);
	
create table order_history (
	acc_id int primary key,
	order_num int not null,
	prod_id int not null,
	quantity int not null,
	foreign key(acc_id) references customer_account,
	unique (order_num, prod_id),
	foreign key(prod_id) references product,
	check(not quantity < 0));
go