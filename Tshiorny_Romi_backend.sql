--USE master
--DROP DATABASE Tshiorny_Romi_db

CREATE DATABASE Tshiorny_Romi_db
GO

USE Tshiorny_Romi_db
GO

CREATE TABLE ChemicalComposition(
	ChemicalID INT PRIMARY KEY,

	Hydrogen FLOAT,
	Helium FLOAT,
	Lithium FLOAT,
	Carbon FLOAT,
	Nitrogen FLOAT,
	Oxygen FLOAT,
	Iron FLOAT,
	Silver Float,
	Gold FLOAT,
	[Lead] FLOAT,
)

CREATE TABLE GalaxyCluster
(
	ClusterID INT PRIMARY KEY IDENTITY(1,1),

	ClusterName VARCHAR(50) UNIQUE NOT NULL,
	RightAscension CHAR(9), /*FORMAT: ##H##M##S*/
	Declination CHAR(9), /*FORMAT: ##D##M##S*/
	Mass FLOAT,
	GalaxyCount FLOAT

)

INSERT INTO GalaxyCluster (ClusterName,Mass,GalaxyCount) VALUES ('Virgo Supercluster',10000000000000000,100000)
SELECT * FROM GalaxyCluster

CREATE TABLE GalaxyGroup
(
	GroupID INT PRIMARY KEY IDENTITY(1,1),

	GroupName VARCHAR(50) UNIQUE NOT NULL,
	RightAscension CHAR(9), /*FORMAT: ##H##M##S*/
	Declination CHAR(9), /*FORMAT: ##D##M##S*/
	Mass FLOAT,
	GalaxyCount FLOAT,
	ClusterID INT REFERENCES GalaxyCluster(ClusterID) ON UPDATE CASCADE

)

INSERT INTO GalaxyGroup(GroupName,Mass,GalaxyCount,ClusterID) VALUES ('Local Group',2000000000000,30,1)
SELECT * FROM GalaxyGroup

CREATE TABLE Galaxy
(
	GalaxyID INT PRIMARY KEY IDENTITY(1,1),

	GalaxyName VARCHAR(50) UNIQUE NOT NULL,
	RightAscension VARCHAR(12), /*FORMAT: ##H##M##S*/
	Declination VARCHAR(12), /*FORMAT: ##D##M##S*/
	Distance FLOAT,
	Size FLOAT,
	Mass FLOAT,
	Magnitude FLOAT,
	Redshift FLOAT,
	StarCount FLOAT,
	DateDiscovered DATE,
	GroupID INT REFERENCES GalaxyGroup(GroupID) ON UPDATE CASCADE

)

INSERT INTO Galaxy(GalaxyName, RightAscension, Declination, Distance, Size, Mass, Magnitude, StarCount, DateDiscovered,GroupID)
	VALUES('Milky Way', '17H45M40S','-29D0M28S',27700,100000,0.21*10000000000000,20.2,400000000000,'1610',1)

SELECT * FROM Galaxy

CREATE TABLE Nebula
(
	NebulaID INT IDENTITY(1,1),
	GalaxyID INT REFERENCES Galaxy(GalaxyID) ON UPDATE CASCADE,

	NebulaName VARCHAR(50) NOT NULL,
	RightAscension CHAR(9), /*FORMAT: ##H##M##S*/
	Declination CHAR(9), /*FORMAT: ##D##M##S*/
	Distance FLOAT,
	DateDiscovered DATE,
	PRIMARY KEY(NebulaID, GalaxyID)

)

CREATE TABLE RogueObject
(
	ObjectID INT IDENTITY(1,1) PRIMARY KEY,

	ObjectName VARCHAR(50) NOT NULL,
	RightAscension CHAR(9), /*FORMAT: ##H##M##S*/
	Declination CHAR(9), /*FORMAT: ##D##M##S*/
	Distance FLOAT,
	Mass FLOAT,
	Age Float CHECK (Age < 14000000000),
	DateDiscovered DATE,
	GalaxyID INT REFERENCES Galaxy(GalaxyID)
)

CREATE TABLE SolarSystem
(
	SystemID INT IDENTITY(1,1),
	GalaxyID INT REFERENCES Galaxy(GalaxyID) ON UPDATE CASCADE,

	SystemName VARCHAR(50) NOT NULL,
	RightAscension CHAR(9), /*FORMAT: ##H##M##S*/
	Declination CHAR(9), /*FORMAT: ##D##M##S*/
	Distance FLOAT,
	PlanetCount FLOAT,
	StarCount FLOAT,
	DistanceFromGalaxyCenter FLOAT,
	PRIMARY KEY(SystemID, GalaxyID)
)

INSERT INTO SolarSystem(SystemName,PlanetCount,StarCount,GalaxyID) VALUES('Solar System',8,25800,1)
SELECT * FROM SolarSystem

CREATE TABLE BlackHole
(
	BlackHoleID INT IDENTITY(1,1),
	SystemID INT,
	GalaxyID INT,

	BlackHoleName VARCHAR(50) NOT NULL,
	Mass FLOAT,
	AngularMomentum FLOAT,
	Age Float CHECK (Age < 14000000000),
	DateDiscovered DATE,
	FOREIGN KEY (SystemID,GalaxyID) REFERENCES SolarSystem(SystemID,GalaxyID) ON UPDATE CASCADE,
	PRIMARY KEY (BlackHoleID,SystemID,GalaxyID)
)
CREATE TABLE Asteroid
(	
	AsteroidID INT IDENTITY(2,2) PRIMARY KEY,

	AsteroidName VARCHAR(50) NOT NULL,
	Mass FLOAT,
	OrbitDistance FLOAT,
	OrbitPeriod FLOAT,
	Eccentricity FLOAT,
	DateDiscovered DATE,
	ChemicalID INT REFERENCES ChemicalComposition(ChemicalID),
	SystemID INT,
	GalaxyID INT,
	FOREIGN KEY (SystemID,GalaxyID) REFERENCES SolarSystem(SystemID,GalaxyID)
)
CREATE TABLE Comet
(	
	CometID INT IDENTITY(2,2) PRIMARY KEY,

	CometName VARCHAR(50) NOT NULL,
	Mass FLOAT,
	OrbitDistance FLOAT,
	OrbitPeriod FLOAT,
	Eccentricity FLOAT,
	DateDiscovered DATE,
	ChemicalID INT REFERENCES ChemicalComposition(ChemicalID),
	SystemID INT,
	GalaxyID INT,
	FOREIGN KEY (SystemID,GalaxyID) REFERENCES SolarSystem(SystemID,GalaxyID)
)
CREATE TABLE Star
(
	StarID INT IDENTITY(1,1),
	SystemID INT,
	GalaxyID INT,

	StarName VARCHAR(50) NOT NULL,
	Mass FLOAT,
	Radius FLOAT,
	Luminosity FLOAT,
	Temperature FLOAT,
	Magnitude FLOAT,
	Age Float CHECK (Age < 14000000000),
	DateDiscovered DATE,
	ChemicalID INT REFERENCES ChemicalComposition(ChemicalID),
	FOREIGN KEY (SystemID,GalaxyID) REFERENCES SolarSystem(SystemID,GalaxyID) ON UPDATE CASCADE,
	PRIMARY KEY (StarID,SystemID,GalaxyID)
)

INSERT INTO ChemicalComposition(ChemicalID,Hydrogen,Helium,Oxygen,Carbon,Iron) VALUES(1,0.7346,0.2485,0.77,0.29,0.16)
INSERT INTO Star(StarName,Mass,Radius,Luminosity,Temperature,Magnitude,Age,ChemicalID,SystemID,GalaxyID) 
	VALUES('The Sun',1,695700,3.828e26, 1.7e7 ,4.83, 4.603e9,1,1,1)
SELECT * FROM Star

CREATE TABLE Cephied(
	StarID INT,
	SystemID INT,
	GalaxyID INT,

	VariableType VARCHAR(20),
	RadialVelocity FLOAT,

	FOREIGN KEY (StarID,SystemID,GalaxyID) REFERENCES Star(StarID,SystemID,GalaxyID) ON UPDATE CASCADE,
	PRIMARY KEY (StarID,SystemID,GalaxyID)
)

CREATE TABLE Planet
(
	PlanetID INT IDENTITY(1,1),
	SystemID INT,
	GalaxyID INT,

	PlanetName VARCHAR(50) NOT NULL,
	Mass FLOAT,
	Radius FLOAT,
	RotationalPeriod FLOAT,
	OribitalPeriod FLOAT,
	DistanceFromCenter FLOAT,
	Age Float CHECK (Age < 14000000000),
	DateDiscovered DATE,
	isDwarf BIT,
	ChemicalID INT REFERENCES ChemicalComposition(ChemicalID),
	FOREIGN KEY (SystemID,GalaxyID) REFERENCES SolarSystem(SystemID,GalaxyID) ON UPDATE CASCADE,
	PRIMARY KEY (PlanetID,SystemID,GalaxyID),

	CONSTRAINT DwarfCheck CHECK (isDwarf = 0 OR isDwarf = 1)
)

INSERT INTO ChemicalComposition(ChemicalID,Nitrogen,Oxygen,Carbon) VALUES(2,0.7808,0.2095,0.000413)
INSERT INTO Planet(PlanetName,Mass,Radius,RotationalPeriod,OribitalPeriod,DistanceFromCenter,Age,isDwarf,ChemicalID,SystemID,GalaxyID) 
	VALUES('Earth',5.97237e24,6371,0.99726968,365.256363004,150e6,4.543e9,0,2,1,1)
SELECT * FROM Planet

CREATE TABLE Satellite
(
	SatelliteID INT IDENTITY(1,1),
	PlanetID INT,
	SystemID INT,
	GalaxyID INT,

	DistanceFromPlanet FLOAT,
	Mass FLOAT,
	OrbitalPeriod FLOAT,
	isNatural BIT,
	ChemicalID INT REFERENCES ChemicalComposition(ChemicalID),
	FOREIGN KEY (PlanetID,SystemID,GalaxyID) REFERENCES Planet(PlanetID,SystemID,GalaxyID) ON UPDATE CASCADE,
	PRIMARY KEY (SatelliteID,PlanetID,SystemID,GalaxyID),

	CONSTRAINT NaturalCheck CHECK (isNatural = 0 OR isNatural = 1)
)

CREATE TABLE ArtificialSatellite
(
	SatelliteID INT,
	PlanetID INT,
	SystemID INT,
	GalaxyID INT,

	[Name] VARCHAR(50) NOT NULL,
	DateLaunched DATE NOT NULL,
	Country	VARCHAR(50) NOT NULL,

	FOREIGN KEY (SatelliteID,PlanetID,SystemID,GalaxyID) 
		REFERENCES Satellite(SatelliteID,PlanetID,SystemID,GalaxyID) ON UPDATE CASCADE,
	PRIMARY KEY (SatelliteID,PlanetID,SystemID,GalaxyID),
)

CREATE TABLE NaturalSatellite
(
	SatelliteID INT,
	PlanetID INT,
	SystemID INT,
	GalaxyID INT,

	[Name] VARCHAR(50) NOT NULL,
	Radius FLOAT,
	RotationalPeriod FLOAT,
	DateDiscovered DATE,

	FOREIGN KEY (SatelliteID,PlanetID,SystemID,GalaxyID) 
		REFERENCES Satellite(SatelliteID,PlanetID,SystemID,GalaxyID) ON UPDATE CASCADE,
	PRIMARY KEY (SatelliteID,PlanetID,SystemID,GalaxyID),
)

INSERT INTO Satellite(PlanetID,SystemID,GalaxyID,DistanceFromPlanet,Mass,isNatural)
	VALUES(1,1,1,384500,7.342e22,1)
INSERT INTO NaturalSatellite(SatelliteID,PlanetID,SystemID,GalaxyID,Radius,RotationalPeriod,[Name])
	VALUES(1,1,1,1,1737.4,0.99726968,'The Moon')

SELECT * FROM Satellite
SELECT * FROM NaturalSatellite
SELECT * FROM ArtificialSatellite

DELETE FROM Satellite WHERE SatelliteID = 5

