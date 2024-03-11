INSERT INTO USERS(NAME, EMAIL) VALUES ('updateName', 'updateName@user.com');
INSERT INTO USERS(NAME, EMAIL) VALUES ('user', 'user@user.com');
INSERT INTO USERS(NAME, EMAIL) VALUES ('other', 'other@other.com');
INSERT INTO USERS(NAME, EMAIL) VALUES ('practicum', 'practicum@yandex.ru');

INSERT INTO ITEMS (NAME,DESCRIPTION,AVAILABLE,OWNER_ID,REQUEST_ID) VALUES
	 ('Аккумуляторная дрель','Аккумуляторная дрель + аккумулятор',true,1,NULL),
	 ('Отвертка','Аккумуляторная отвертка',true,2,NULL),
	 ('Клей Момент','Тюбик суперклея марки Момент',true,2,NULL),
	 ('Кухонный стол','Стол для празднования',true,4,NULL);

INSERT INTO BOOKINGS (START_BOOKING,END_BOOKING,ITEM_ID,BOOKER_ID,STATUS) VALUES
	 ('2024-02-16 03:31:51','2024-02-16 03:31:52',2,1,'APPROVED'),
	 ('2024-02-17 03:31:49','2024-02-18 03:31:49',2,1,'APPROVED'),
	 ('2024-02-17 03:31:50','2024-02-17 04:31:50',1,2,'REJECTED'),
	 ('2024-02-16 04:31:50','2024-02-16 05:31:50',2,3,'APPROVED'),
	 ('2024-02-16 03:31:58','2024-02-17 03:31:55',3,1,'REJECTED'),
	 ('2024-02-16 03:31:58','2024-02-16 03:31:59',2,1,'APPROVED'),
	 ('2024-02-26 03:31:56','2024-02-27 03:31:56',1,3,'APPROVED'),
	 ('2024-02-16 03:31:58','2024-02-16 04:31:56',4,1,'APPROVED');

INSERT INTO COMMENTS (TEXT,ITEM_ID,AUTHOR_ID,CREATED) VALUES
     	 ('Add comment from user1',2,1,'2024-02-16 03:32:03.040069');
