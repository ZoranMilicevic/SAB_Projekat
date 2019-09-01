
CREATE TABLE [Article]
( 
	[Id]                 integer  IDENTITY ( 1,1 )  NOT NULL ,
	[Price]              decimal(10,3)  NULL ,
	[Amount]             integer  NULL ,
	[Name]               varchar(100)  NULL ,
	[ShopId]             integer  NULL 
)
go

ALTER TABLE [Article]
	ADD CONSTRAINT [XPKArticle] PRIMARY KEY  CLUSTERED ([Id] ASC)
go

CREATE TABLE [Buyer]
( 
	[Id]                 integer  IDENTITY ( 1,1 )  NOT NULL ,
	[Name]               varchar(100)  NULL ,
	[Credit]             decimal(10,3)  NULL ,
	[CityId]             integer  NULL 
)
go

ALTER TABLE [Buyer]
	ADD CONSTRAINT [XPKBuyer] PRIMARY KEY  CLUSTERED ([Id] ASC)
go

CREATE TABLE [City]
( 
	[Id]                 integer  IDENTITY ( 1,1 )  NOT NULL ,
	[Name]               varchar(100)  NULL 
)
go

ALTER TABLE [City]
	ADD CONSTRAINT [XPKCity] PRIMARY KEY  CLUSTERED ([Id] ASC)
go

CREATE TABLE [Line]
( 
	[Distance]           integer  NULL ,
	[CityId1]            integer  NULL ,
	[CityId2]            integer  NULL ,
	[Id]                 integer  IDENTITY ( 1,1 )  NOT NULL 
)
go

ALTER TABLE [Line]
	ADD CONSTRAINT [XPKLine] PRIMARY KEY  CLUSTERED ([Id] ASC)
go

CREATE TABLE [OrderItem]
( 
	[OrderId]            integer  NULL ,
	[ArticleId]          integer  NULL ,
	[Id]                 integer  IDENTITY ( 1,1 )  NOT NULL ,
	[Count]              integer  NULL 
)
go

ALTER TABLE [OrderItem]
	ADD CONSTRAINT [XPKOrderItem] PRIMARY KEY  CLUSTERED ([Id] ASC)
go

CREATE TABLE [Orderr]
( 
	[State]              varchar(20)  NULL ,
	[BuyerId]            integer  NULL ,
	[Id]                 integer  IDENTITY ( 1,1 )  NOT NULL ,
	[SentTime]           datetime  NULL ,
	[RecievedTime]       datetime  NULL ,
	[FinalPrice]         decimal(10,3)  NULL ,
	[ExpectedArrivalTime] datetime  NULL ,
	[Location]           integer  NULL ,
	[ExpectedAssembleTime] datetime  NULL ,
	[NextLocation]       integer  NULL ,
	[TwoPercent]         integer  NULL 
)
go

ALTER TABLE [Orderr]
	ADD CONSTRAINT [XPKOrderr] PRIMARY KEY  CLUSTERED ([Id] ASC)
go

CREATE TABLE [Shop]
( 
	[Id]                 integer  IDENTITY ( 1,1 )  NOT NULL ,
	[Discount]           integer  NULL ,
	[CityId]             integer  NULL 
)
go

ALTER TABLE [Shop]
	ADD CONSTRAINT [XPKShop] PRIMARY KEY  CLUSTERED ([Id] ASC)
go

CREATE TABLE [TransactionBuyer]
( 
	[Amount]             decimal(10,3)  NULL ,
	[OrderId]            integer  NULL ,
	[Id]                 integer  NOT NULL 
)
go

ALTER TABLE [TransactionBuyer]
	ADD CONSTRAINT [XPKTransactionBuyer] PRIMARY KEY  CLUSTERED ([Id] ASC)
go

CREATE TABLE [Transactionn]
( 
	[Id]                 integer  IDENTITY ( 1,1 )  NOT NULL 
)
go

ALTER TABLE [Transactionn]
	ADD CONSTRAINT [XPKTransactionn] PRIMARY KEY  CLUSTERED ([Id] ASC)
go

CREATE TABLE [TransactionSystem]
( 
	[Amount]             decimal(10,3)  NULL ,
	[OrderId]            integer  NULL ,
	[ShopId]             integer  NULL ,
	[Id]                 integer  NOT NULL 
)
go

ALTER TABLE [TransactionSystem]
	ADD CONSTRAINT [XPKTransactionSystem] PRIMARY KEY  CLUSTERED ([Id] ASC)
go


ALTER TABLE [Article]
	ADD CONSTRAINT [R_14] FOREIGN KEY ([ShopId]) REFERENCES [Shop]([Id])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [Buyer]
	ADD CONSTRAINT [R_6] FOREIGN KEY ([CityId]) REFERENCES [City]([Id])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [Line]
	ADD CONSTRAINT [R_15] FOREIGN KEY ([CityId1]) REFERENCES [City]([Id])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [Line]
	ADD CONSTRAINT [R_16] FOREIGN KEY ([CityId2]) REFERENCES [City]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [OrderItem]
	ADD CONSTRAINT [R_11] FOREIGN KEY ([OrderId]) REFERENCES [Orderr]([Id])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [OrderItem]
	ADD CONSTRAINT [R_13] FOREIGN KEY ([ArticleId]) REFERENCES [Article]([Id])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [Orderr]
	ADD CONSTRAINT [R_10] FOREIGN KEY ([BuyerId]) REFERENCES [Buyer]([Id])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [Shop]
	ADD CONSTRAINT [R_4] FOREIGN KEY ([CityId]) REFERENCES [City]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [TransactionBuyer]
	ADD CONSTRAINT [R_19] FOREIGN KEY ([OrderId]) REFERENCES [Orderr]([Id])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [TransactionBuyer]
	ADD CONSTRAINT [R_25] FOREIGN KEY ([Id]) REFERENCES [Transactionn]([Id])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [TransactionSystem]
	ADD CONSTRAINT [R_20] FOREIGN KEY ([OrderId]) REFERENCES [Orderr]([Id])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [TransactionSystem]
	ADD CONSTRAINT [R_21] FOREIGN KEY ([ShopId]) REFERENCES [Shop]([Id])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [TransactionSystem]
	ADD CONSTRAINT [R_26] FOREIGN KEY ([Id]) REFERENCES [Transactionn]([Id])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go
