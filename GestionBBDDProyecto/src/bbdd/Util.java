package bbdd;

import java.sql.Time;
import java.time.LocalDate;

public class Util {

	// Indica si la opción
	public static boolean esOpcionValida(int inicio, int last, String opcion) {
		if (esEntero(opcion)) {
			int numero = Integer.parseInt(opcion);
			return inicio <= numero && numero <= last;
		} else if (opcion.equals("salir")) {
			return true;
		}
		return false;
	}

	public static boolean esEntero(String numero) {
		try {
			Integer.parseInt(numero);
			return true;
		} catch (NumberFormatException ne) {
			return false;
		}
	}

	public static boolean validarEmail(String email) {
		if (email == null || email.trim().isEmpty()) {
			return false;
		}
		String emailClean = email.trim();
		String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
		return emailClean.matches(regex);
	}

	public static boolean consultaValida(String input) {
		return !input.contains(";") && !input.toLowerCase().contains("delete");
	}

	public static boolean fechaValida(String input) {
		return input.matches("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$");
	}

	public static boolean rangoFechasValido(String fechaInicio, String fechaFin) {
		if (!fechaValida(fechaInicio) || !fechaValida(fechaFin)) {
			System.out.println("El formato de fechas no es correcto");
			return false;
		}

		LocalDate ldateInicio = LocalDate.parse(fechaInicio);
		LocalDate ldateFin = LocalDate.parse(fechaFin);

		if (!ldateInicio.isBefore(ldateFin)) {
			System.out.println("La fecha de inicio debe ir antes que la de fin");
			return false;
		}
		return true;
	}

	public static boolean timeValido(String input) {
		return input.matches("^(?:(?:([01]?\\d|2[0-3]):)?([0-5]?\\d):)?([0-5]?\\d)$");
	}

	public static boolean rangoTimeValido(String horaInicio, String horaFin) {
		if (!timeValido(horaInicio) || !timeValido(horaFin)) {
			System.out.println("El formato de horas no es correcto");
			return false;
		}

		Time timeInicio = Time.valueOf(horaInicio);
		Time timeFin = Time.valueOf(horaFin);

		if (!timeInicio.before(timeFin)) {
			System.out.println("La hora inicial debe ir antes que la de fin");
			return false;
		}
		return true;
	}

	public static boolean entraRangoFechas(String fecha, String fechaInicio, String fechaFin) {
		if (!fechaValida(fecha)) {
			return false;
		}

		LocalDate dateInicio = LocalDate.parse(fechaInicio);
		LocalDate dateFin = LocalDate.parse(fechaFin);
		LocalDate date = LocalDate.parse(fecha);
		return (date.isAfter(dateInicio) && date.isBefore(dateFin)) || date.isEqual(dateInicio)
				|| date.isEqual(dateFin);
	}

}
