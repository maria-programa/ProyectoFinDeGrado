package bbdd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class GestionViajes {
	public static final String BBDD = "apus";
	public static final String USER = "root";
	public static final String SERVER = "jdbc:mysql://localhost:3306/";
	public static final String PWD = "";

	public static void main(String[] args) {
		try {
			// Establecemos conexion
			System.out.println("Estableciendo conexión con APUS");
			Connection conexion = DriverManager.getConnection(SERVER + BBDD, USER, PWD);
			ConsultasApus consulta = new ConsultasApus(conexion);
			System.out.println("CONECTADO");

			Scanner sc = new Scanner(System.in);
			System.out.println("************* BIENVENIDO A APUS *************");

			// BUCLE EJECUTADAO MIENTRAS EL USUARIO NO ACCEDA O QUIERA SALIR
			String opcion = "";
			boolean acceso = false;
			do {
				// MENU INICIAL, VALIDA EL INPUT DEL USUARIO
				do {
					System.out.print("Escoja una opción:\n" + "1.- Acceder\n" + "2.- Registrarse\n" + "- Salir -\n"
							+ "Opcion: ");
					opcion = sc.nextLine().toLowerCase();
					if (!Util.esOpcionValida(1, 2, opcion)) {
						System.out.println("Opción no válida");
					}
					System.out.println();
				} while (!Util.esOpcionValida(1, 2, opcion));

				// SWITCH DE REGISTRO O ACCESO
				switch (opcion) {
				case "1":
					acceso = true;
					break;
				case "2":
					acceso = true;
					break;
				}

			} while (!opcion.equals("salir") || !acceso);

			// Se ejecuta el bucle mientras el cliente no escoja salir
			do {
				// Menu inicial de inicio de sesion

				String usuario = "";
				String contrasena = "";
				String usuarioApus = "";

				// do while not valido o intentos superados
				switch (opcion) {
				case "1":
					System.out.println("------------- Acceso de usuario -------------");
					System.out.print("Nombre de usuario: ");
					usuario = sc.nextLine();
					System.out.print("Contraseña: ");
					contrasena = sc.nextLine();

					// Buscar en la bbdd que exite el usuario con su contrasena
					if (!consulta.accederUsuario(usuario, contrasena)) {
						System.out.println("El usuario o contraseña no son válidos");
					}

					break;
				case "2":
					String correo = "";
					String nombre = "";
					String urlImagen = "";
					String correoApus = "";

					System.out.println("------------- Registro de usuario -------------");
					// Comprobar correo válido e inexistente
					do {
						System.out.print("Correo electrónico: ");
						correo = sc.nextLine();
						correoApus = consulta.buscarCorreoUsuario(correo);

						if (correoApus != null) {
							System.out.println("El correo ya existe");
						}
					} while (!Util.validarEmail(correo) || correoApus != null);

					// Comprobar usuario disponible
					do {
						System.out.print("Nombre de usuario: ");
						usuario = sc.nextLine();
						usuarioApus = consulta.buscarNombreUsuario(usuario);

						if (usuarioApus != null) {
							System.out.println("El nombre de usuario ya existe");
						}
					} while (!Util.consultaValida(usuario) || usuarioApus != null);

					// Constraseña
					do {
						System.out.print("Contraseña: ");
						contrasena = sc.nextLine();
					} while (!Util.consultaValida(contrasena));

					// Nombre o alias
					do {
						System.out.print("Nombre: ");
						nombre = sc.nextLine();
					} while (!Util.consultaValida(nombre));

					// Imagen
					do {
						System.out.print("Imagen: ");
						urlImagen = sc.nextLine();
					} while (!Util.consultaValida(urlImagen));

					consulta.crearUsuario(correo, usuario, contrasena, nombre, urlImagen);
					break;
				}

				// Consultamos los viajes del usuario
				System.out.println("Accediendo a los viajes de " + usuario + "...\n");
				boolean tieneViajes = consulta.mostrarViajesUsuario(usuario);

				// Menu de viajes
				if (!tieneViajes) {
					opcion = "1";
				} else {
					do {
						System.out.print("Escoja una opción:\n" + "1.- Crear viaje\n" + "2.- Ver viaje\n"
								+ "3.- Editar viaje\n" + "4.- Eliminar viaje\n" + "- Salir -\n" + "Opcion: ");
						opcion = sc.nextLine().toLowerCase();
						if (!Util.esOpcionValida(1, 4, opcion)) {
							System.out.println("Opción no válida");
						}
						System.out.println();
					} while (!Util.esOpcionValida(1, 4, opcion));
				}

				switch (opcion) {
				case "1":
					System.out.println("------------- Crear Viaje -------------");
					System.out.print("Nombre: ");
					String nombreViaje = sc.nextLine();
					String fechaInicioViaje = "";
					do {
						System.out.print("Fecha de inicio (aaaa-mm-dd): ");
						fechaInicioViaje = sc.nextLine();

					} while (!Util.fechaValida(fechaInicioViaje));
					String fechaFinViaje = "";
					do {
						System.out.print("Fecha de fin (aaaa-mm-dd): ");
						fechaFinViaje = sc.nextLine();
					} while (!Util.fechaValida(fechaFinViaje));
					System.out.print("Imagen: ");
					String urlImagenViaje = sc.nextLine();

					consulta.crearViaje(usuario, nombreViaje, fechaInicioViaje, fechaFinViaje, urlImagenViaje);
					break;
				case "2":
					String idViaje = "";
					do {
						System.out.print("Introduce el id del viaje: ");
						idViaje = sc.nextLine();

						if (Util.esEntero(idViaje)
								&& !consulta.buscarViajeUsuario(usuarioApus, Integer.parseInt(idViaje))) {
							System.out.println("El viaje seleccionado no existe");
						}
					} while (!Util.esEntero(idViaje)
							|| !consulta.buscarViajeUsuario(usuarioApus, Integer.parseInt(idViaje)));

					break;
				case "3":
					break;
				case "4":
					break;
				}

			} while (!opcion.equals("salir"));

			// Creación del usuario

			// Acceso usuario

			// Creacion viajes

			// Acceso viajes

			// Creación Alojamientos
			// Acceso alojamientos

			// Creación Fotos
			// Acceso Fotos

			// Creación Billetes
			// Acceso Billetes

			// Creacion itinerario (automatico según los dias del viaje)

			// Acceso al itinerario

			// Creacion Actividades

			// Acceso actividad

			sc.close();
		} catch (SQLException e) {
			System.out.println("NO SE PUDO ESTABLECER CONEXION");
		}
	}

}
