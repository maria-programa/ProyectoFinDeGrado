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
	private int itinerario;

	public static final String BBDD = "apus";
	public static final String USER = "root";
	public static final String SERVER = "jdbc:mysql://localhost:3306/";
	public static final String PWD = "";

	public Apus() {
		try {
			conect();
			init();
			exit = false;
			sc.close();
		} catch (SQLException e) {
			System.out.println("NO SE PUDO ESTABLECER CONEXION CON APUS");
		}
	}

	private void conect() throws SQLException {
		System.out.println("Estableciendo conexión con APUS");
		Connection connection = DriverManager.getConnection(SERVER + BBDD, USER, PWD);
		bbdd = new ConsultasApus(connection);
		System.out.println("CONECTADO");
	}

	private void init() throws SQLException {
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
			do {
				System.out.print("Nombre de usuario: ");
				user = sc.nextLine();
			} while (!Util.consultaValida(user));
			do {
				System.out.print("Contraseña: ");
				pwd = sc.nextLine();
			} while (!Util.consultaValida(pwd));

			// Buscar en la bbdd que exite el usuario con su contrasena
			access = bbdd.accederUsuario(user, pwd);
			if (!access) {
				System.out.println("El usuario o contraseña no son válidos\n");
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
			System.out.print("Correo electrónico*: ");
			correo = sc.nextLine();
			if (!Util.consultaValida(correo)) {
				System.out.println("Por favor introduzca un nombre válido");
				continue;
			}
			correoApus = bbdd.getCorreoUsuario(correo);
			if (correoApus != null) {
				System.out.println("El correo ya existe");
			}
		} while (!Util.validarEmail(correo) || correoApus != null);

		// Comprobar usuario disponible
		do {
			System.out.print("Nombre de usuario*: ");
			user = sc.nextLine();
			if (!Util.consultaValida(user)) {
				System.out.println("Por favor introduzca un nombre válido");
				continue;
			}
			userApus = bbdd.getNombreUsuario(user);
			if (userApus != null) {
				System.out.println("El nombre de usuario ya existe");
			}
		} while (!Util.consultaValida(user) || userApus != null);

		// Constraseña
		do {
			System.out.print("Contraseña*: ");
			pwd = sc.nextLine();
		} while (!Util.consultaValida(pwd));

		// Nombre o alias
		do {
			System.out.print("Nombre*: ");
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
							+ "3.- Añadir Alojamiento\n" + "4.- Ver Alojamientos\n" + "5.- Editar viaje\n"
							+ "6.- Eliminar viaje\n" + "- Salir -\n" + "Opcion: ");
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
				addAlojamiento();
				break;
			case "4":
				readAlojamiento();
				break;
			case "5":
				updateViaje();
				break;
			case "6":
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

		String nombreViaje = "";
		do {
			System.out.print("Nombre*: ");
			nombreViaje = sc.nextLine();
		} while (nombreViaje.isEmpty() || !Util.consultaValida(nombreViaje));

		String fechaInicioViaje = "";
		String fechaFinViaje = "";
		do {
			System.out.print("Fecha de inicio* (aaaa-mm-dd): ");
			fechaInicioViaje = sc.nextLine();
			System.out.print("Fecha de fin* (aaaa-mm-dd): ");
			fechaFinViaje = sc.nextLine();
		} while (!Util.rangoFechasValido(fechaInicioViaje, fechaFinViaje));

		String urlImagenViaje = "";
		do {
			System.out.print("Imagen: ");
			urlImagenViaje = sc.nextLine();
		} while (!Util.consultaValida(urlImagenViaje));
		bbdd.crearViaje(user, nombreViaje, fechaInicioViaje, fechaFinViaje, urlImagenViaje);
		System.out.println("Viaje creado con éxito :)\n");
	}

	public boolean readViaje() throws SQLException {
		System.out.println("------------- Ver Viaje -------------");
		String idViaje = "";
		do {
			System.out.print("Id del viaje*: ");
			idViaje = sc.nextLine();
		} while (!Util.esEntero(idViaje));

		if (!bbdd.buscarViajeUsuario(user, Integer.parseInt(idViaje))) {
			System.out.println("El viaje no existe");
			System.out.println("Volviendo atrás...\n");
			return false;
		} else {
			viaje = Integer.parseInt(idViaje);
			System.out.println("Accediendo al viaje " + viaje + "...\n");
			bbdd.mostrarItinerario(viaje);
			return true;
		}
	}

	public void updateViaje() throws SQLException {
		System.out.println("------------- Editar Viaje -------------");
		String idViaje = "";
		do {
			System.out.print("Id del viaje*: ");
			idViaje = sc.nextLine();
		} while (!Util.esEntero(idViaje));

		if (!bbdd.buscarViajeUsuario(user, Integer.parseInt(idViaje))) {
			System.out.println("El viaje no existe");
			System.out.println("Volviendo atrás...\n");
		} else {
			viaje = Integer.parseInt(idViaje);
			System.out.println("Accediendo al viaje " + viaje + "...\n");
			System.out.println("Escribe para editar, en blanco para mantener");

			String nombreViaje = "";
			do {
				System.out.print("Nombre: ");
				nombreViaje = sc.nextLine();
			} while (!Util.consultaValida(nombreViaje));

			String fechaInicioViaje = "";
			String fechaFinViaje = "";
			String fechaInicioBBDD = bbdd.getFechaInicioViaje(viaje);
			String fechaFinBBDD = bbdd.getFechaFinViaje(viaje);
			boolean rangoValido = true;
			do {
				rangoValido = true;
				System.out.print("Fecha de inicio (aaaa-mm-dd): ");
				fechaInicioViaje = sc.nextLine();
				System.out.print("Fecha de fin (aaaa-mm-dd): ");
				fechaFinViaje = sc.nextLine();

				if (!fechaInicioViaje.isEmpty() && !fechaFinViaje.isEmpty()
						&& !Util.rangoFechasValido(fechaInicioViaje, fechaFinViaje)) {
					rangoValido = false;
				} else if (!fechaInicioViaje.isEmpty() && fechaFinViaje.isEmpty()
						&& !Util.rangoFechasValido(fechaInicioViaje, fechaFinBBDD)) {
					rangoValido = false;
				} else if (fechaInicioViaje.isEmpty() && !fechaFinViaje.isEmpty()
						&& !Util.rangoFechasValido(fechaInicioBBDD, fechaFinViaje)) {
					rangoValido = false;
				}
			} while (!rangoValido);

			String urlImagenViaje = "";
			do {
				System.out.print("Imagen: ");
				urlImagenViaje = sc.nextLine();
			} while (!Util.consultaValida(urlImagenViaje));

			bbdd.editarViaje(viaje, nombreViaje, fechaInicioViaje, fechaFinViaje, urlImagenViaje);
			System.out.println("Viaje modificado con éxito :)\n");
			bbdd.mostrarItinerario(viaje);
		}
	}

	public void deleteViaje() throws SQLException {
		System.out.println("------------- Eliminar Viaje -------------");
		String idViaje = "";
		do {
			System.out.print("Id del viaje*: ");
			idViaje = sc.nextLine();
		} while (!Util.esEntero(idViaje));

		if (!bbdd.buscarViajeUsuario(user, Integer.parseInt(idViaje))) {
			System.out.println("El viaje no existe");
			System.out.println("Volviendo atrás...\n");
		} else {
			System.out.println("Eliminando viaje " + idViaje + "...");
			bbdd.eliminarViaje(Integer.parseInt(idViaje));
			System.out.println("Viaje eliminado con éxito :)\n");
		}
	}

	public void addAlojamiento() throws SQLException {
		System.out.println("------------- Añadir Alojamiento -------------");

		String idViaje = "";
		do {
			System.out.print("Id del viaje*: ");
			idViaje = sc.nextLine();
		} while (!Util.esEntero(idViaje));

		if (!bbdd.buscarViajeUsuario(user, Integer.parseInt(idViaje))) {
			System.out.println("El viaje no existe");
			System.out.println("Volviendo atrás...\n");
		} else {
			viaje = Integer.parseInt(idViaje);
			bbdd.mostrarItinerario(viaje);
			String nombreAlojamiento = "";
			do {
				System.out.print("Nombre*: ");
				nombreAlojamiento = sc.nextLine();
			} while (nombreAlojamiento.isEmpty() || !Util.consultaValida(nombreAlojamiento));
			String direccion = "";
			do {
				System.out.print("Direccion*: ");
				direccion = sc.nextLine();
			} while (direccion.isEmpty() || !Util.consultaValida(direccion));

			String ciudad = "";
			do {
				System.out.print("Ciudad: ");
				ciudad = sc.nextLine();
			} while (!Util.consultaValida(ciudad));
			String pais = "";
			do {
				System.out.print("Pais: ");
				pais = sc.nextLine();
			} while (!Util.consultaValida(pais));

			String fechaEntrada = "";
			String fechaSalida = "";
			String fechaInicioViaje = bbdd.getFechaInicioViaje(viaje);
			String fechaFinViaje = bbdd.getFechaFinViaje(viaje);
			do {
				do {
					System.out.print("Fecha de entrada* (aaaa-mm-dd): ");
					fechaEntrada = sc.nextLine();
					if (!Util.entraRangoFechas(fechaEntrada, fechaInicioViaje, fechaFinViaje)) {
						System.out.println("La fecha no se encuentra en el rango del viaje");
					}
				} while (!Util.entraRangoFechas(fechaEntrada, fechaInicioViaje, fechaFinViaje));
				do {
					System.out.print("Fecha de salida* (aaaa-mm-dd): ");
					fechaSalida = sc.nextLine();
					if (!Util.entraRangoFechas(fechaSalida, fechaInicioViaje, fechaFinViaje)) {
						System.out.println("La fecha no se encuentra en el rango del viaje");
					}
				} while (!Util.entraRangoFechas(fechaSalida, fechaInicioViaje, fechaFinViaje));
			} while (!Util.rangoFechasValido(fechaEntrada, fechaSalida));

			String contacto = "";
			do {
				System.out.print("Contacto: ");
				contacto = sc.nextLine();
			} while (!Util.consultaValida(contacto));
			String notas = "";
			do {
				System.out.print("Notas: ");
				notas = sc.nextLine();
			} while (!Util.consultaValida(notas));

			bbdd.crearAlojamiento(viaje, nombreAlojamiento, direccion, ciudad, pais, fechaEntrada, fechaSalida,
					contacto, notas);
			System.out.println("Alojamiento añadido con éxito :)\n");
		}
	}

	public void readAlojamiento() throws SQLException {
		System.out.println("------------- Ver Alojamientos del Viaje -------------");
		String idViaje = "";
		do {
			System.out.print("Id del viaje*: ");
			idViaje = sc.nextLine();
		} while (!Util.esEntero(idViaje));

		if (!bbdd.buscarViajeUsuario(user, Integer.parseInt(idViaje))) {
			System.out.println("El viaje no existe");
			System.out.println("Volviendo atrás...\n");
		} else {
			viaje = Integer.parseInt(idViaje);
			System.out.println("Accediendo al viaje " + viaje + "...\n");
			bbdd.mostrarAlojamientos(viaje);
		}
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
			deleteActividad();
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
			System.out.print("Fecha de la actividad* (aaaa-mm-dd): ");
			fechaActividad = sc.nextLine();
		} while (!Util.fechaValida(fechaActividad));

		int idItinerario = bbdd.getIdItinerario(viaje, fechaActividad);
		if (idItinerario == 0) {
			System.out.println("La fecha no corresponde con el viaje indicado");
			System.out.println("Volviendo atrás...\n");
		} else {
			String direccion = "";
			do {
				System.out.print("Direccion*: ");
				direccion = sc.nextLine();
			} while (direccion.isEmpty() || !Util.consultaValida(direccion));
			String ciudad = "";
			do {
				System.out.print("Ciudad: ");
				ciudad = sc.nextLine();
			} while (!Util.consultaValida(ciudad));
			String pais = "";
			do {
				System.out.print("Pais: ");
				pais = sc.nextLine();
			} while (!Util.consultaValida(pais));
			String time = "";
			do {
				System.out.print("Hora de inicio* (hh:mm:ss): ");
				time = sc.nextLine();
			} while (!Util.timeValido(time));
			String duracion = "";
			do {
				System.out.print("Duracion (hh:mm:ss): ");
				duracion = sc.nextLine();
			} while (!duracion.isEmpty() && !Util.timeValido(duracion));
			bbdd.crearActividad(direccion, ciudad, pais, time, duracion, idItinerario);
			System.out.println("Actividad creada con éxito :)\n");
		}

	}

	public void uploadFoto() throws SQLException {
		System.out.println("------------- Subir foto -------------");
		String fechaActividad = "";
		do {
			System.out.print("Fecha de la actividad* (aaaa-mm-dd): ");
			fechaActividad = sc.nextLine();
		} while (!Util.fechaValida(fechaActividad));

		int idItinerario = bbdd.getIdItinerario(viaje, fechaActividad);
		if (idItinerario == 0) {
			System.out.println("La fecha no corresponde con el viaje indicado");
			System.out.println("Volviendo atrás...\n");
		} else {
			String nombre = "";
			do {
				System.out.print("Nombre: ");
				nombre = sc.nextLine();
			} while (!Util.consultaValida(nombre));
			String ruta = "";
			do {
				System.out.print("Ruta*: ");
				ruta = sc.nextLine();
			} while (ruta.isEmpty() || !Util.consultaValida(ruta));
			bbdd.subirFoto(nombre, ruta, idItinerario);
			System.out.println("Foto subida con éxito :)\n");
		}
	}

	public void uploadBillete() throws SQLException {
		System.out.println("------------- Subir Billete -------------");
		String fechaActividad = "";
		do {
			System.out.print("Fecha de la actividad* (aaaa-mm-dd): ");
			fechaActividad = sc.nextLine();
		} while (!Util.fechaValida(fechaActividad));

		int idItinerario = bbdd.getIdItinerario(viaje, fechaActividad);
		if (idItinerario == 0) {
			System.out.println("La fecha no corresponde con el viaje indicado");
			System.out.println("Volviendo atrás...\n");
		} else {

			String nombre = "";
			do {
				System.out.print("Nombre*: ");
				nombre = sc.nextLine();
			} while (nombre.isEmpty() || !Util.consultaValida(nombre));
			String transporte = "";
			do {
				System.out.print("Trasporte: ");
				transporte = sc.nextLine();
			} while (!Util.consultaValida(transporte));
			String origen = "";
			do {
				System.out.print("Origen*: ");
				origen = sc.nextLine();
			} while (origen.isEmpty() || !Util.consultaValida(origen));
			String destino = "";
			do {
				System.out.print("Destino*: ");
				destino = sc.nextLine();
			} while (destino.isEmpty() || !Util.consultaValida(destino));
			String horaSalida = "";
			do {
				System.out.print("Hora de salida* (hh:mm:ss): ");
				horaSalida = sc.nextLine();
			} while (!Util.timeValido(horaSalida));
			String horaLlegada = "";
			do {
				System.out.print("Hora de llegada (hh:mm:ss): ");
				horaLlegada = sc.nextLine();
			} while (!horaLlegada.isEmpty() && !Util.timeValido(horaLlegada));
			String compania = "";
			do {
				System.out.print("Compañía: ");
				compania = sc.nextLine();
			} while (!Util.consultaValida(compania));
			String identificador = "";
			do {
				System.out.print("Identificador: ");
				identificador = sc.nextLine();
			} while (!Util.consultaValida(identificador));

			String ruta = "";
			do {
				System.out.print("Ruta*: ");
				ruta = sc.nextLine();
			} while (ruta.isEmpty() || !Util.consultaValida(ruta));
			bbdd.subirBillete(nombre, transporte, origen, destino, horaSalida, horaLlegada, compania, identificador,
					ruta, idItinerario);
			System.out.println("Foto subida con éxito :)\n");
		}
	}

	public boolean readActividad() throws SQLException {
		System.out.println("------------- Actividades del Itinerario -------------");

		String fechaItinerario = "";
		do {
			System.out.print("Fecha del itinerario* (aaaa-mm-dd): ");
			fechaItinerario = sc.nextLine();
		} while (!Util.fechaValida(fechaItinerario));

		itinerario = bbdd.getIdItinerario(viaje, fechaItinerario);
		if (itinerario == 0) {
			System.out.println("La fecha no corresponde con el viaje indicado");
			System.out.println("Volviendo atrás...\n");
			return false;
		} else {
			System.out.println("Accediendo al itinerario del día " + fechaItinerario + "...\n");
			if (!bbdd.mostrarActividad(itinerario)) {
				System.out.println("Volviendo atrás...\n");
				return false;
			}
			return true;
		}
	}

	public void deleteActividad() throws SQLException {
		if (!readActividad()) {
			return;
		}

		System.out.println("------------- Eliminar Actividad -------------");
		String idActividad = "";
		do {
			System.out.print("Id de la actividad*: ");
			idActividad = sc.nextLine();
		} while (!Util.esEntero(idActividad));

		if (!bbdd.eliminarActividad(itinerario, Integer.parseInt(idActividad))) {
			System.out.println("La actividad especificada no corresponde al itinerario");
			System.out.println("Volviendo atrás...\n");
			return;
		}
		System.out.println("Actividad eliminada con éxito :)\n");
	}
}
