Create Table [dbo].[Attendance] (
    DateEntered datetime    not null,
    UserId      Varchar(50) not null,
    ServerId    Varchar(50) not null,
    Nickname    Varchar(50) not null,
    Constraint PK_Attendance Primary Key ([DateEntered], [UserId])
);

create table [dbo].[GuildCategories]
(
    Id   UniqueIdentifier Constraint DF_GuildCategories_Id Default NewId(),
        Constraint PK_GuildCategories Primary Key ([Id]),
    CategoryName Varchar(50) not null,
    GuildId      Varchar(50) not null,
    Constraint Udx_GuildCategories_CategoryName_GuildID Unique ([CategoryName], [GuildID])
);

Create Table [dbo].[CategoryOwners] (
    Id         UniqueIdentifier Constraint DF_CategoryOwners_Id Default NewId(),
        Constraint PK_CategoryOwners Primary Key ([Id]),
    CategoryId UniqueIdentifier not null,
    OwnerId    Varchar(50) not null,
    Constraint FK_CategoryOwners_GuildCategories_CategoryID
        Foreign Key ([CategoryId]) References GuildCategories ([Id])
);

Create Index Idx_CategoryOwners_OwnerID_CategoryID On CategoryOwners ([OwnerID], [CategoryID]);

create table [dbo].[CategoryRoles]
(
    Id         UniqueIdentifier Constraint DF_CategoryRoles_Id Default NewId(),
        Constraint PK_CategoryRoles Primary Key ([Id]),
    CategoryId UniqueIdentifier not null,
    RoleId     Varchar(50) null,
    RoleName   Varchar(50) not null,
    constraint Udx_CategoryRoles_CategoryID_RoleID unique ([CategoryID], [RoleID]),
    constraint Fk_CategoryRoles_GuildCategories_CategoryID
        foreign key ([CategoryID]) references [GuildCategories] ([Id])
);

create table [dbo].[NolebotExceptions]
(
    Id         UniqueIdentifier Constraint DF_NolebotExceptions_Id Default NewId(),
        Constraint PK_NolebotExceptions Primary Key ([Id]),
    Timestamp  DateTime2      null,
    Message    Varchar(max)   null,
    OrigClass  Varchar(255)   null,
    LineNum    int            null,
    MethodName Varchar(255)   null,
    RootCause  Varchar(1000)  null,
    Username   Varchar(100)   null
);
