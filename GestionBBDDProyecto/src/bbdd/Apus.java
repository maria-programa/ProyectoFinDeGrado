package bbdd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class Apus {
	private Scanner sc;
	private ConsultasApus bbdd;
	private String user;
	private int viaje;
	private boolean exit;

	public static final String BBDD = "apus";
	public static final String USER = "root";
	public static final String SERVER = "jdbc:mysql://localhost:3306/";
	public static final String PWD = "";

	public Apus() {
		try {
			run();
			exit = false;
			sc.close();
		} catch (SQLException e) {
			System.out.println("NO SE PUDO ESTABLECER CONEXION CON APUS");
		}
	}

	private void run() throws SQLException {
		conect();
		innit();
	}

	private void conect() throws SQLException {
		System.out.println("Estableciendo conexión con APUS");
		Connection connection = DriverManager.getConnection(SERVER + BBDD, USER, PWD);
		bbdd = new ConsultasApus(connection);
		System.out.println("CONECTADO");
	}

	private void innit() throws SQLException {
		System.out.println("************* BIENVENIDO A APUS *************");
		sc = new Scanner(System.in);
		mainMenu();
		if (exit) {
			return;
		}
		menuViajes();
		if (exit) {
			return;
		}
	}

	private void mainMenu() throws SQLException {
		String opcion = "";
		boolean access = false;
		do {
			do {
				System.out.print(
						"Escoja una opción:\n" + "1.- Acceder\n" + "2.- Registrarse\n" + "- Salir -\n" + "Opcion: ");
				opcion = sc.nextLine().toLowerCase();
				if (!Util.esOpcionValida(1, 2, opcion)) {
					System.out.println("Opción no válida");
				}
				System.out.println();
			} while (!Util.esOpcionValida(1, 2, opcion));

			String pwd = "";
			String userApus = "";
			// SWITCH DE REGISTRO O ACCESO
			switch (opcion) {
			case "1":
				userAccess(pwd, userApus);
				System.out.println();
				access = true;
				break;
			case "2":
				userRegister(pwd, userApus);
				access = true;
				break;
			case "salir":
				exit = true;
				break;
			}
		} while (!exit && !access);
	}

	private void userAccess(String pwd, String userApus) throws SQLException {
		boolean access = false;
		do {
			System.out.println("------------- Acceso de usuario -------------");
			System.out.print("Nombre de usuario: ");
			user = sc.nextLine();
			System.out.print("Contraseña: ");
			pwd = sc.nextLine();

			// Buscar en la bbdd que exite el usuario con su contrasena
			access = bbdd.accederUsuario(user, pwd);
			if (!access) {
				System.out.println("El usuario o contraseña no son válidos");
			}
		} while (!access);
	}

	private void userRegister(String pwd, String userApus) throws SQLException {
		String correo = "";
		String nombre = "";
		String urlImagen = "";
		String correoApus = "";

		System.out.println("------------- Registro de usuario -------------");
		// Comprobar correo válido e inexistente
		do {
			System.out.print("Correo electrónico: ");
			correo = sc.nextLine();
			if (!Util.consultaValida(correo)) {
				System.out.println("Por favor introduzca un nombre válido");
				continue;
			}
			correoApus = bbdd.buscarCorreoUsuario(correo);
			if (correoApus != null) {
				System.out.println("El correo ya existe");
			}
		} while (!Util.validarEmail(correo) || correoApus != null);

		// Comprobar usuario disponible
		do {
			System.out.print("Nombre de usuario: ");
			user = sc.nextLine();
			if (!Util.consultaValida(user)) {
				System.out.println("Por favor introduzca un nombre válido");
				continue;
			}
			userApus = bbdd.buscarNombreUsuario(user);
			if (userApus != null) {
				System.out.println("El nombre de usuario ya existe");
			}
		} while (!Util.consultaValida(user) || userApus != null);

		// Constraseña
		do {
			System.out.print("Contraseña: ");
			pwd = sc.nextLine();
		} while (!Util.consultaValida(pwd));

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

		bbdd.crearUsuario(correo, user, pwd, nombre, urlImagen);
	}

	private void menuViajes() throws SQLException {
		System.out.println("Accediendo a los viajes de " + user + "...");
		do {
			boolean tieneViajes = bbdd.mostrarViajesUsuario(user);

			String opcion = "";
			if (!tieneViajes) {
				opcion = "1";
			} else {
				do {
					System.out.print("Escoja una opción:\n" + "1.- Crear viaje\n" + "2.- Ver itinerario del viaje\n"
							+ "3.- Ver Alojamientos\n" + "4.- Editar viaje\n" + "5.- Eliminar viaje\n" + "- Salir -\n"
							+ "Opcion: ");
					opcion = sc.nextLine().toLowerCase();
					if (!Util.esOpcionValida(1, 5, opcion)) {
						System.out.println("Opción no válida");
					}
					System.out.println();
				} while (!Util.esOpcionValida(1, 5, opcion));
			}

			switch (opcion) {
			case "1":
				createViaje();
				break;
			case "2":
				if (readViaje()) {
					menuItinerario();
				}
				break;
			case "3":

				break;
			case "4":
				updateViaje();
				break;
			case "5":
				deleteViaje();
				break;
			case "salir":
				exit = true;
				break;
			}
		} while (!exit);

	}

	public void createViaje() throws SQLException {
		System.out.println("------------- Crear Viaje -------------");

		System.out.print("Nombre: ");
		String nombreViaje = sc.nextLine();

		String fechaInicioViaje = "";
		String fechaFinViaje = "";
		do {
			System.out.print("Fecha de inicio (aaaa-mm-dd): ");
			fechaInicioViaje = sc.nextLine();
			System.out.print("Fecha de fin (aaaa-mm-dd): ");
			fechaFinViaje = sc.nextLine();
		} while (!Util.rangoFechasValido(fechaInicioViaje, fechaFinViaje));

		System.out.print("Imagen: ");
		String urlImagenViaje = sc.nextLine();

		bbdd.crearViaje(user, nombreViaje, fechaInicioViaje, fechaFinViaje, urlImagenViaje);
	}

	public boolean readViaje() throws SQLException {
		System.out.println("------------- Ver Viaje -------------");
		String idViaje = "";
		do {
			System.out.print("Id del viaje: ");
			idViaje = sc.nextLine();
		} while (!Util.esEntero(idViaje));

		if (!bbdd.buscarViajeUsuario(user, Integer.parseInt(idViaje))) {
			System.out.println("El viaje no existe");
			return false;
		} else {
			viaje = Integer.parseInt(idViaje);
			System.out.println("Accediendo al viaje " + viaje + "...\n");
			bbdd.mostrarItinerario(viaje);
			return true;
		}
	}

	public void updateViaje() {

	}

	public void deleteViaje() throws SQLException {
		System.out.println("------------- Eliminar Viaje -------------");
		String idViaje = "";
		do {
			System.out.print("Id del viaje: ");
			idViaje = sc.nextLine();
		} while (!Util.esEntero(idViaje));

		if (!bbdd.buscarViajeUsuario(user, Integer.parseInt(idViaje))) {
			System.out.println("El viaje no existe");
		} else {
			System.out.println("Eliminando viaje " + idViaje + "...");
			bbdd.eliminarViaje(Integer.parseInt(idViaje));
			System.out.println("Viaje eliminado con éxito :)");
		}
	}

	public void addAlojamiento() {

	}

	public void menuItinerario() throws SQLException {
		String opcion = "";
		do {
			System.out.print("Escoja una opción:\n" + "1.- Crear Actividad\n" + "2.- Subir billete\n"
					+ "3.- Subir Foto\n" + "4.- Eliminar Actividad\n" + "- Salir -\n" + "Opcion: ");
			opcion = sc.nextLine().toLowerCase();
			if (!Util.esOpcionValida(1, 4, opcion)) {
				System.out.println("Opción no válida");
			}
			System.out.println();
		} while (!Util.esOpcionValida(1, 4, opcion));

		switch (opcion) {
		case "1":
			createActividad();
			break;
		case "2":
			uploadBillete();
			break;
		case "3":
			uploadFoto();
			break;
		case "4":
			break;
		case "salir":
			exit = true;
			break;
		}

	}

	public void createActividad() throws SQLException {
		System.out.println("------------- Crear Actividad -------------");
		String fechaActividad = "";
		do {
			System.out.print("Fecha de la actividad (aaaa-mm-dd): ");
			fechaActividad = sc.nextLine();
		} while (!Util.fechaValida(fechaActividad));

		int idItinerario = bbdd.idItinerario(viaje, fechaActividad);
		if (idItinerario == 0) {
			System.out.println("La fecha no corresponde con el viaje indicado");
		} else {
			System.out.print("Direccion: ");
			String direccion = sc.nextLine();
			System.out.print("Ciudad: ");
			String ciudad = sc.nextLine();
			System.out.print("Pais: ");
			String pais = sc.nextLine();
			String time = "";
			do {
				System.out.print("Hora de inicio (hh:mm:ss): ");
				time = sc.nextLine();
			} while (!Util.timeValido(time));
			String duracion = "";
			do {
				System.out.print("Duracion (hh:mm:ss): ");
				duracion = sc.nextLine();
			} while (!Util.timeValido(duracion));
			bbdd.crearActividad(direccion, ciudad, pais, time, duracion, idItinerario);
			System.out.println("Actividad creada con éxito :)");
		}

	}

	public void uploadFoto() throws SQLException {
		System.out.println("------------- Subir foto -------------");
		String fechaActividad = "";
		do {
			System.out.print("Fecha de la actividad (aaaa-mm-dd): ");
			fechaActividad = sc.nextLine();
		} while (!Util.fechaValida(fechaActividad));

		int idItinerario = bbdd.idItinerario(viaje, fechaActividad);
		if (idItinerario == 0) {
			System.out.println("La fecha no corresponde con el viaje indicado");
		} else {
			System.out.print("Nombre: ");
			String nombre = sc.nextLine();
			System.out.print("Ruta: ");
			String ruta = sc.nextLine();
			bbdd.subirFoto(nombre, ruta, idItinerario);
			System.out.println("Foto subida con éxito :)");
		}
	}

	public void uploadBillete() throws SQLException {
		System.out.println("------------- Subir Billete -------------");
		String fechaActividad = "";
		do {
			System.out.print("Fecha de la actividad (aaaa-mm-dd): ");
			fechaActividad = sc.nextLine();
		} while (!Util.fechaValida(fechaActividad));

		int idItinerario = bbdd.idItinerario(viaje, fechaActividad);
		if (idItinerario == 0) {
			System.out.println("La fecha no corresponde con el viaje indicado");
		} else {
			System.out.print("Nombre: ");
			String nombre = sc.nextLine();
			System.out.print("Trasporte: ");
			String transporte = sc.nextLine();
			System.out.print("Origen: ");
			String origen = sc.nextLine();
			System.out.print("Destino: ");
			String destino = sc.nextLine();
			String horaSalida = "";
			do {
				System.out.print("Hora de salida (hh:mm:ss): ");
				horaSalida = sc.nextLine();
			} while (!Util.timeValido(horaSalida));
			String horaLlegada = "";
			do {
				System.out.print("Hora de llegada (hh:mm:ss): ");
				horaLlegada = sc.nextLine();
			} while (!Util.timeValido(horaLlegada));
			System.out.print("Compañía: ");
			String compania = sc.nextLine();
			System.out.print("Identificador: ");
			String identificador = sc.nextLine();
			System.out.print("Ruta: ");
			String ruta = sc.nextLine();
			bbdd.subirBillete(nombre, transporte, origen, destino, horaSalida, horaLlegada, compania, identificador,
					ruta, idItinerario);
			System.out.println("Foto subida con éxito :)");
		}
	}

	public void deleteActividad() {

	}
}
