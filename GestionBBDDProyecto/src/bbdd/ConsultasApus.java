package bbdd;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ConsultasApus {
	private Connection conexion;

	public ConsultasApus(Connection conexion) {
		this.conexion = conexion;
	}

	// GETTERS NECESARIOS
	// ///////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * Obtiene el id de la tabla Usuario según el nombre_usuario
	 */
	public int getIdUsuario(String usuario) throws SQLException {
		String consulta = "select id from usuario where nombre_usuario = \"" + usuario + "\";";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ResultSet res = ps.executeQuery();

		return res.next() ? res.getInt(1) : null;
	}

	/*
	 * Obtiene el correo de la tabla Usuario según el correo útil para comprobar si
	 * ya existe porque es un campo unico
	 */
	public String getCorreoUsuario(String correo) throws SQLException {
		String consulta = "select correo_electronico from usuario where correo_electronico = \"" + correo + "\";";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ResultSet res = ps.executeQuery();

		return res.next() ? res.getString(1) : null;
	}

	/*
	 * Obtiene el nombre_usuario de la tabla Usuario según el nombre_usuario útil
	 * para comprobar si ya existe porque es un campo unico
	 */
	public String getNombreUsuario(String usuario) throws SQLException {
		String consulta = "select nombre_usuario from usuario where nombre_usuario = \"" + usuario + "\";";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ResultSet res = ps.executeQuery();

		return res.next() ? res.getString(1) : null;
	}

	/*
	 * Obtiene el nombre del viaje de la tabla Viaje según el id
	 */
	public String getNombreViaje(int idViaje) throws SQLException {
		String consulta = "select nombre from viaje where id = " + idViaje + ";";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ResultSet res = ps.executeQuery();

		return res.next() ? res.getString(1) : null;
	}

	/*
	 * Obtiene la fecha de inicio de la tabla Viaje según el id
	 */
	public String getFechaInicioViaje(int idViaje) throws SQLException {
		String consulta = "select fecha_inicio from viaje where id = " + idViaje + ";";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ResultSet res = ps.executeQuery();

		return res.next() ? String.valueOf(res.getDate(1)) : null;
	}

	/*
	 * Obtiene la fecha de la tabla Viaje según el id
	 */
	public String getFechaFinViaje(int idViaje) throws SQLException {
		String consulta = "select fecha_fin from viaje where id = " + idViaje + ";";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ResultSet res = ps.executeQuery();

		return res.next() ? String.valueOf(res.getDate(1)) : null;
	}

	/*
	 * Obtiene la imagen de la tabla Viaje según el id
	 */
	public String getImagenViaje(int idViaje) throws SQLException {
		String consulta = "select imagen from viaje where id = " + idViaje + ";";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ResultSet res = ps.executeQuery();

		return res.next() ? res.getString(1) : null;
	}

	/*
	 * Obtiene el id del último viaje creado Útil para crear automáticamente el
	 * itinerario según los días del viaje
	 */
	public int idUltimoViaje() throws SQLException {
		String consulta = "select id from viaje;";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ResultSet res = ps.executeQuery();

		return res.last() ? res.getInt(1) : null;
	}

	/*
	 * Obtiene el id del tinerario según el viaje y la fecha Útil para comprobar que
	 * el input para acceder al itinerario según la fecha corrsponde al viaje
	 * especificado
	 */
	public int getIdItinerario(int idViaje, String fecha) throws SQLException {
		String consulta = "select id from itinerario where id_viaje = " + idViaje + " and fecha = \"" + fecha + "\";";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ResultSet res = ps.executeQuery();

		return res.last() ? res.getInt(1) : 0;
	}
	// ///////////////////////////////////////////////////////////////////////////////////////////

	// METODOS INSERT
	// ///////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * Insert Usuario
	 */
	public void crearUsuario(String correo, String usuario, String contrasenna, String nombre, String imagen)
			throws SQLException {
		String consulta = "insert into usuario(correo_electronico, nombre_usuario, contrasenna, nombre, imagen) "
				+ "values(?,?,?,?,?);";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ps.setString(1, correo);
		ps.setString(2, usuario);
		ps.setString(3, contrasenna);
		ps.setString(4, nombre);
		ps.setString(5, imagen);

		ps.executeUpdate();
		System.out.println("Usuario creado con éxito :)");
	}

	/*
	 * Insert Viaje
	 */
	public void crearViaje(String usuario, String nombre, String fechaInicio, String fechaFin, String imagen)
			throws SQLException {
		String consulta = "insert into viaje(fecha_creacion, nombre, fecha_inicio, fecha_fin, imagen, creador) "
				+ "values(?,?,?,?,?,?);";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ps.setDate(1, Date.valueOf(LocalDate.now()));
		ps.setString(2, nombre);
		ps.setDate(3, Date.valueOf(fechaInicio));
		ps.setDate(4, Date.valueOf(fechaFin));
		ps.setString(5, imagen);
		ps.setInt(6, getIdUsuario(usuario));

		ps.executeUpdate();
		int idViaje = idUltimoViaje();
		// Hacemos la relación en la tabla usuario_viaje
		asociarViajeUsuario(idViaje, usuario);
		// Creación automática del itinerario según los días
		crearItinerarioViaje(idViaje, fechaInicio, fechaFin);
	}

	/*
	 * Insert usuario_viaje útil al crear un viaje
	 */
	public void asociarViajeUsuario(int idViaje, String usuario) throws SQLException {
		int idUsuario = getIdUsuario(usuario);
		String consulta = "insert into usuario_viaje values(?,?)";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ps.setInt(1, idUsuario);
		ps.setInt(2, idViaje);
		ps.executeUpdate();
	}

	/*
	 * Insert itinerario según días del viaje útil al crear el viaje
	 */
	public void crearItinerarioViaje(int idViaje, String fechainicio, String fechaFin) throws SQLException {
		// Primero calculamos los dias
		LocalDate ldateInicio = LocalDate.parse(fechainicio);
		LocalDate ldateFin = LocalDate.parse(fechaFin);
		int dias = (int) ldateInicio.until(ldateFin, ChronoUnit.DAYS);

		String consulta = "insert into itinerario(fecha, id_viaje) values(?,?);";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		for (int i = 0; i <= dias; i++) {
			ps.setDate(1, Date.valueOf(ldateInicio.plusDays(i)));
			ps.setInt(2, idViaje);
			ps.executeUpdate();
		}
	}

	/*
	 * Insert itinerario desde la fecha indicada hasta llegar al rango indicado útil
	 * al editar las fechas del viaje
	 */
	private void annadirItinerarioViaje(int idViaje, LocalDate fechaInicio, int dias) throws SQLException {
		String consulta = "insert into itinerario(fecha, id_viaje) values(?,?);";
		PreparedStatement ps = conexion.prepareStatement(consulta);

		for (int i = 0; i <= dias; i++) {
			Date date = Date.valueOf(fechaInicio.plusDays(i));
			if (!existeItinerario(date, idViaje)) {
				ps.setDate(1, date);
				ps.setInt(2, idViaje);
				ps.executeUpdate();
			}
		}
	}

	/*
	 * Insert alojamiento
	 */
	public void crearAlojamiento(int idViaje, String nombre, String direccion, String ciudad, String pais,
			String fechaEntrada, String fechaSalida, String contacto, String notas) throws SQLException {
		String consulta = "insert into alojamiento(nombre, direccion, ciudad, pais, fecha_entrada, fecha_salida, contacto, notas, id_viaje) "
				+ "values(?,?,?,?,?,?,?,?,?);";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ps.setString(1, nombre);
		ps.setString(2, direccion);
		ps.setString(3, ciudad);
		ps.setString(4, pais);
		ps.setDate(5, Date.valueOf(fechaEntrada));
		ps.setDate(6, Date.valueOf(fechaSalida));
		ps.setString(7, contacto);
		ps.setString(8, notas);
		ps.setInt(9, idViaje);

		ps.executeUpdate();
	}

	/*
	 * Insert actividad
	 */
	public void crearActividad(String titulo, String descripcion, String direccion, String ciudad, String pais,
			String horaInicio, String horaFin, int idItinerario) throws SQLException {
		String consulta = "insert into actividad(titulo, descripcion, direccion, ciudad, pais, hora_inicio, hora_fin, id_itinerario) "
				+ "values(?,?,?,?,?,?,?,?)";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ps.setString(1, titulo);
		ps.setString(2, descripcion);
		ps.setString(3, direccion);
		ps.setString(4, ciudad);
		ps.setString(5, pais);
		ps.setTime(6, Time.valueOf(horaInicio));
		ps.setTime(7, Time.valueOf(horaFin.isEmpty() ? "00:00:00" : horaFin));
		ps.setInt(8, idItinerario);
		ps.executeUpdate();
	}

	/*
	 * Insert foto
	 */
	public void subirFoto(String nombre, String ruta, int idItinerario) throws SQLException {
		String consulta = "insert into foto(nombre, ruta_archivo, id_itinerario) values(?,?,?)";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ps.setString(1, nombre);
		ps.setString(2, ruta);
		ps.setInt(3, idItinerario);
		ps.executeUpdate();
	}

	/*
	 * Insert Billete
	 */
	public void subirBillete(String nombre, String transporte, String origen, String destino, String horaSalida,
			String horaLlegada, String compania, String identificador, String ruta, int idItinerario)
			throws SQLException {
		String consulta = "insert into billete(nombre, transporte, origen, destino, hora_salida, hora_llegada, "
				+ "compannia, identificador, ruta_archivo, id_itinerario) values(?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ps.setString(1, nombre);
		ps.setString(2, transporte);
		ps.setString(3, origen);
		ps.setString(4, destino);
		ps.setTime(5, Time.valueOf(horaSalida));
		ps.setTime(6, Time.valueOf(horaLlegada.isEmpty() ? "00:00:00" : horaLlegada));
		ps.setString(7, compania);
		ps.setString(8, identificador);
		ps.setString(9, ruta);
		ps.setInt(10, idItinerario);
		ps.executeUpdate();
	}
	// ///////////////////////////////////////////////////////////////////////////////////////////

	// METODOS DELETE
	// ///////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * Delete Viaje y sus relaciones según su id
	 */
	public void eliminarViaje(int idViaje) throws SQLException {
		String consulta = "";
		PreparedStatement ps = null;
		ResultSet res = null;

		// Eliminar Alojamiento
		consulta = "delete from alojamiento where id_viaje = " + idViaje + ";";
		ps = conexion.prepareStatement(consulta);
		ps.executeUpdate();

		// Eliminar usuario_viaje
		consulta = "delete from usuario_viaje where id_viaje = " + idViaje + ";";
		ps = conexion.prepareStatement(consulta);
		ps.executeUpdate();

		// Eliminar itinerarios y sus relaciones
		eliminarItinerarioViaje(idViaje);

		// Por último eliminamos viaje
		consulta = "delete from viaje where id = " + idViaje + ";";
		ps = conexion.prepareStatement(consulta);
		ps.executeUpdate();
	}

	/*
	 * Delete itinerarios y sus relaciones según el id_viaje Útil cuando se elimina
	 * un viaje
	 */
	public void eliminarItinerarioViaje(int idViaje) throws SQLException {
		String consulta = "";
		PreparedStatement ps = null;
		ResultSet res = null;

		// Seleccionamos los itinerarios del viaje
		consulta = "select id from itinerario where id_viaje = " + idViaje + ";";
		ps = conexion.prepareStatement(consulta);
		res = ps.executeQuery();
		String consulta1 = "";
		PreparedStatement ps1 = null;
		while (res.next()) {
			// Eliminamos las actividades
			consulta1 = "delete from actividad where id_itinerario = " + res.getInt(1) + ";";
			ps1 = conexion.prepareStatement(consulta1);
			ps1.executeUpdate();
			// Eliminamos las fotos
			consulta1 = "delete from foto where id_itinerario = " + res.getInt(1) + ";";
			ps1 = conexion.prepareStatement(consulta1);
			ps1.executeUpdate();
			// Eliminamos los billetes
			consulta1 = "delete from billete where id_itinerario = " + res.getInt(1) + ";";
			ps1 = conexion.prepareStatement(consulta1);
			ps1.executeUpdate();
		}

		// Eliminamos los itinerarios
		consulta = "delete from itinerario where id_viaje = " + idViaje + ";";
		ps = conexion.prepareStatement(consulta);
		ps.executeUpdate();
	}

	/*
	 * Delete itinerarios y sus relaciones según la fecha y el id_viaje Útil cuando
	 * se edita un viaje, borra los itinerarios fuera del nuevo rango de fechas
	 */
	public void eliminarItinerarioFecha(Date fecha, int idViaje) throws SQLException {
		String consulta = "";
		PreparedStatement ps = null;
		ResultSet res = null;

		// Eliminar itinerarios
		consulta = "select id from itinerario where fecha = ? and id_viaje = ?;";
		ps = conexion.prepareStatement(consulta);
		ps.setDate(1, fecha);
		ps.setInt(2, idViaje);
		res = ps.executeQuery();
		String consulta1 = "";
		PreparedStatement ps1 = null;
		while (res.next()) {
			// Eliminamos las actividades
			consulta1 = "delete from actividad where id_itinerario = " + res.getInt(1) + ";";
			ps1 = conexion.prepareStatement(consulta1);
			ps1.executeUpdate();
			// Eliminamos las fotos
			consulta1 = "delete from foto where id_itinerario = " + res.getInt(1) + ";";
			ps1 = conexion.prepareStatement(consulta1);
			ps1.executeUpdate();
			// Eliminamos los billetes
			consulta1 = "delete from billete where id_itinerario = " + res.getInt(1) + ";";
			ps1 = conexion.prepareStatement(consulta1);
			ps1.executeUpdate();
		}

		// Eliminamos los itinerarios
		consulta = "delete from itinerario where fecha = ? and id_viaje = ?;";
		ps = conexion.prepareStatement(consulta);
		ps.setDate(1, fecha);
		ps.setInt(2, idViaje);
		ps.executeUpdate();
	}

	/*
	 * Delete Actividad según el id_itinerario y el id_actividad Útil cuando se
	 * elimina un una actividad, comprueba si la actividad señalada corresponde al
	 * itinerario protegiendo otras actividades existentes
	 */
	public boolean eliminarActividad(int idItinerario, int idActividad) throws SQLException {
		String consulta = "delete from actividad where id_itinerario = ? and id = ?;";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ps.setInt(1, idItinerario);
		ps.setInt(2, idActividad);

		return ps.executeUpdate() != 0;
	}
	// ///////////////////////////////////////////////////////////////////////////////////////////

	// METODOS UPDATE
	// ///////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * Update Viaje, los campos vacios no se modificaran
	 */
	public void editarViaje(int idViaje, String nombre, String fechaInicio, String fechaFin, String imagen)
			throws SQLException {
		editarItinerarioViaje(idViaje, fechaInicio, fechaFin);

		String consulta = "update viaje set nombre = ?, fecha_inicio = ?, fecha_fin = ?, imagen = ? where id = ?;";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ps.setString(1, nombre.isEmpty() ? getNombreViaje(idViaje) : nombre);
		ps.setDate(2, Date.valueOf(fechaInicio.isEmpty() ? getFechaInicioViaje(idViaje) : fechaInicio));
		ps.setDate(3, Date.valueOf(fechaFin.isEmpty() ? getFechaFinViaje(idViaje) : fechaFin));
		ps.setString(4, imagen.isEmpty() ? getImagenViaje(idViaje) : imagen);
		ps.setInt(5, idViaje);

		ps.executeUpdate();
	}
	// ///////////////////////////////////////////////////////////////////////////////////////////

	// METODOS QUE MUESTRAN TABLAS
	// ///////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * Muestra los viajes del Usuario boolean porque devuelve si tiene viajes o no
	 * útil para no dar otra opción al usuario excepto crear Viaje
	 */
	public boolean mostrarViajesUsuario(String usuario) throws SQLException {
		String consulta = "select viaje.* from usuario join usuario_viaje on usuario.id=usuario_viaje.id_usuario "
				+ "join viaje on usuario_viaje.id_viaje=viaje.id " + "where usuario.nombre_usuario = \"" + usuario
				+ "\";";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ResultSet res = ps.executeQuery();
		ResultSetMetaData rmd = res.getMetaData();

		// Si el usuario no tiene viajes creados
		if (!res.next()) {
			System.out.println("Aún no tiene viajes creados");
			System.out.println();
			return false;
		}
		res.beforeFirst();

		System.out.printf("%5s%20s%20s%20s%20s%40s%10s\n", rmd.getColumnName(1), rmd.getColumnName(2),
				rmd.getColumnName(3), rmd.getColumnName(4), rmd.getColumnName(5), rmd.getColumnName(6),
				rmd.getColumnName(7));
		for (int i = 0; i < 5 + 20 + 20 + 20 + 20 + 40 + 10; i++) {
			System.out.print("*");
		}
		System.out.println();
		while (res.next()) {
			System.out.printf("%5s%20s%20s%20s%20s%40s%10s\n", String.valueOf(res.getInt(1)),
					String.valueOf(res.getDate(2)), res.getString(3), String.valueOf(res.getDate(4)),
					String.valueOf(res.getDate(5)), res.getString(6), String.valueOf(res.getInt(7)));
		}
		System.out.println();
		return true;
	}

	/*
	 * Alojamientos según viaje
	 */
	public void mostrarAlojamientos(int idViaje) throws SQLException {
		String consulta = "select nombre, direccion, ciudad, pais, fecha_entrada, fecha_salida, contacto, notas "
				+ "from alojamiento where id_viaje = " + idViaje + ";";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ResultSet res = ps.executeQuery();
		ResultSetMetaData rmd = res.getMetaData();

		// Si no encuentra alojamientos para ese viaje lo indica y sale
		if (!res.next()) {
			System.out.println("Aún no tiene alojamientos en este viaje.");
			System.out.println();
			return;
		}
		res.beforeFirst();

		System.out.printf("%20s%20s%20s%20s%20s%20s%20s%20s\n", rmd.getColumnName(1), rmd.getColumnName(2),
				rmd.getColumnName(3), rmd.getColumnName(4), rmd.getColumnName(5), rmd.getColumnName(6),
				rmd.getColumnName(7), rmd.getColumnName(8));
		for (int i = 0; i < 160; i++) {
			System.out.print("*");
		}
		System.out.println();
		while (res.next()) {
			System.out.printf("%20s%20s%20s%20s%20s%20s%20s%20s\n", res.getString(1), res.getString(2),
					res.getString(3), res.getString(4), String.valueOf(res.getDate(5)), String.valueOf(res.getDate(6)),
					res.getString(7), res.getString(8));
		}
		System.out.println();
	}

	/*
	 * Itinerario según viaje
	 */
	public void mostrarItinerario(int idViaje) throws SQLException {
		String consulta = "select itinerario.fecha from itinerario join viaje on itinerario.id_viaje=viaje.id "
				+ "where id_viaje = " + idViaje + " order by itinerario.fecha;";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ResultSet res = ps.executeQuery();
		ResultSetMetaData rmd = res.getMetaData();

		System.out.printf("%20s\n", rmd.getColumnName(1));
		for (int i = 0; i < 20; i++) {
			System.out.print("*");
		}
		System.out.println();
		while (res.next()) {
			System.out.printf("%20s\n", String.valueOf(res.getDate(1)));
		}
		System.out.println();
	}

	/*
	 * Actividad según itinerario boolean porque devuelve si tiene actividades o no
	 */
	public boolean mostrarActividad(int idItinerario) throws SQLException {
		String consulta = "select id, direccion, ciudad, pais, hora_inicio, duracion from actividad "
				+ "where id_itinerario = " + idItinerario + ";";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ResultSet res = ps.executeQuery();
		ResultSetMetaData rmd = res.getMetaData();

		if (!res.next()) {
			System.out.println("Aún no tiene actividades creadas");
			System.out.println();
			return false;
		}
		res.beforeFirst();

		System.out.printf("%10s%20s%20s%20s%10s%10s\n", rmd.getColumnName(1), rmd.getColumnName(2),
				rmd.getColumnName(3), rmd.getColumnName(4), rmd.getColumnName(5), rmd.getColumnName(6));
		for (int i = 0; i < 10 + 20 + 20 + 20 + 10 + 10; i++) {
			System.out.print("*");
		}
		System.out.println();
		while (res.next()) {
			System.out.printf("%10s%20s%20s%20s%10s%10s\n", String.valueOf(res.getInt(1)), res.getString(2),
					res.getString(3), res.getString(4), String.valueOf(res.getTime(5)), String.valueOf(res.getTime(6)));
		}
		System.out.println();
		return true;
	}
	// ///////////////////////////////////////////////////////////////////////////////////////////

	// OTROS METODOS
	// ///////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * Comprueba que el usuario y la contraseña coiciden
	 */
	public boolean accederUsuario(String usuario, String constrasenna) throws SQLException {
		if (getNombreUsuario(usuario) == null) {
			return false;
		}

		String consulta = "select contrasenna from usuario where nombre_usuario = \"" + usuario + "\";";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ResultSet res = ps.executeQuery();

		return res.next() ? res.getString(1).equals(constrasenna) : null;
	}

	/*
	 * NO ES UPDATE Elimina y añade los itinerarios de un viaje que al ser editado
	 * cambia su rango de fechas Elimina aquellos que quedan fuera (junto sus
	 * relaciones) Añade los nuevos días
	 */
	public void editarItinerarioViaje(int idViaje, String fechaInicio, String fechaFin) throws SQLException {
		// Nuevo rango de días
		LocalDate inicioNuevo = LocalDate.parse(!fechaInicio.isEmpty() ? fechaInicio : getFechaInicioViaje(idViaje));
		LocalDate finNuevo = LocalDate.parse(!fechaFin.isEmpty() ? fechaFin : getFechaFinViaje(idViaje));
		int rangoNuevo = (int) inicioNuevo.until(finNuevo, ChronoUnit.DAYS);
		// Rango de días Original
		LocalDate inicioOriginal = LocalDate.parse(getFechaInicioViaje(idViaje));
		LocalDate finOriginal = LocalDate.parse(getFechaFinViaje(idViaje));

		// Diferencia de días
		int diasInicio = (int) inicioNuevo.until(inicioOriginal, ChronoUnit.DAYS);
		int diasFin = (int) finNuevo.until(finOriginal, ChronoUnit.DAYS);
		// No ha cambiado los días
		if (diasInicio == 0 && diasFin == 0) {
			return;
		}
		// Si la inicioNuevo después a inicioOriginal (diasInicio < 0)
		// Borramos la diferencia de días desde la fecha_inicio original y fecha_inicio
		// nueva
		if (diasInicio < 0) {
			for (int i = 0; i < -diasInicio; i++) {
				Date date = Date.valueOf(inicioOriginal.plusDays(i));
				if (existeItinerario(date, idViaje)) {
					eliminarItinerarioFecha(date, idViaje);
				}
			}
		}
		// Si el finoNuevo antes a finOriginal (diasFin > 0)
		// Borramos la diferencia de días desde la fecha_fin nueva y fecha_fin original
		if (diasFin > 0) {
			for (int i = 1; i <= diasFin; i++) {
				Date date = Date.valueOf(finNuevo.plusDays(i));
				if (existeItinerario(date, idViaje)) {
					eliminarItinerarioFecha(date, idViaje);
				}
			}
		}
		// En caso de tener nuevos días al inicio o el final
		// los añadiremos al itinerario
		if (diasInicio > 0 || diasFin < 0) {
			annadirItinerarioViaje(idViaje, inicioNuevo, rangoNuevo);
		}
	}

	/*
	 * Comprueba que el itinerario según la fecha y el viaje existe
	 */
	public boolean existeItinerario(Date fecha, int idViaje) throws SQLException {
		String consulta = "select fecha from itinerario where fecha = ? and id_viaje = ?;";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ps.setDate(1, fecha);
		ps.setInt(2, idViaje);
		ResultSet res = ps.executeQuery();

		return res.next();
	}

	/*
	 * Comprueba que el usuario tiene ese viaje útil para no permitir que entre en
	 * otro viaje
	 */

	public boolean buscarViajeUsuario(String usuario, int idViaje) throws SQLException {
		int idUsuario = getIdUsuario(usuario);

		String consulta = "select * from viaje join usuario_viaje on viaje.id=usuario_viaje.id_viaje "
				+ "where id_usuario = " + idUsuario + " " + "and id_viaje = " + idViaje + ";";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ResultSet res = ps.executeQuery();

		return res.first();
	}

}
