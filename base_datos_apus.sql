create database apus;

use apus;

create table usuario(
id int primary key auto_increment,
correo_electronico varchar(50) not null unique,
nombre_usuario varchar(50) not null unique,
contrasenna varchar(100) not null,
nombre varchar(25) not null,
imagen varchar(100),
es_creador boolean
);

create table viaje(
id int primary key auto_increment,
fecha_creacion date not null,
nombre varchar(100) not null,
fecha_inicio date not null,
fecha_fin date not null,
imagen varchar(100)
);

create table usuario_usuario(
id_usuario1 int,
id_usuario2 int,
foreign key(id_usuario1) references usuario(id),
foreign key(id_usuario2) references usuario(id),
primary key(id_usuario1, id_usuario2)
);

create table usuario_viaje(
id_usuario int,
id_viaje int,
foreign key(id_usuario) references usuario(id),
foreign key(id_viaje) references viaje(id),
primary key(id_usuario, id_viaje)
);

create table alojamiento(
id int primary key auto_increment,
nombre varchar(100) not null,
direccion varchar(100) not null,
ciudad varchar(100),
pais varchar(100),
fecha_entrada date not null,
fecha_salida date not null,
contacto varchar(100),
notas varchar(750),
id_viaje int,
foreign key(id_viaje) references viaje(id)
);

create table itinerario(
id int primary key auto_increment,
fecha date not null
);

create table actividad(
id int primary key auto_increment,
direccion varchar(100) not null,
ciudad varchar(100),
pais varchar(100),
hora_inicio time not null,
duracion time,
id_itinerario int,
foreign key(id_itinerario)

);
