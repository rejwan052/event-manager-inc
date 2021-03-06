package it.inc.org.spring.controller;

import it.inc.org.spring.model.DataTableSource;
import it.inc.org.spring.model.EngineUtils;
import it.inc.org.spring.model.Event;
import it.inc.org.spring.model.TypeModel;
import it.inc.org.spring.model.UserModel;
import it.inc.org.spring.service.EventService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class EventManagerController {

	@Autowired
	private EventService eventService;
	
	private static final Logger log = Logger.getLogger(EventManagerController.class);

	/*
	 * **************************************************
	 * TYPES.JSP **************************************************
	 */

	@RequestMapping("/types")
	public ModelAndView showCampiList(Model model) {

		model.addAttribute("itemList", eventService.getTypesList());
		model.addAttribute("item", new TypeModel());

		return new ModelAndView("types", "model", model);
	}

	@RequestMapping(value = "/saveType", method = RequestMethod.POST)
	public @ResponseBody String saveType(@RequestBody TypeModel toAdd) {
		System.out.println("Save Type: " + toAdd);
		eventService.saveType(toAdd);
		return "success";
	}

	/*
	 * **************************************************
	 * USERS.JSP **************************************************
	 */

	@RequestMapping("/users")
	public ModelAndView showUsersList(Model model) {
		System.out.println("Users view call");
		model.addAttribute("userList", eventService.getUserList());
		model.addAttribute("user", new UserModel());

		return new ModelAndView("users", "model", model);
	}

	@RequestMapping(value = "/saveUser", method = RequestMethod.POST)
	public @ResponseBody String saveUser(@RequestBody UserModel toAdd) {
		System.out.println("Save user: " + toAdd);
		eventService.saveUser(toAdd);
		return "success";
	}

	/*
	 * **************************************************
	 * SCHEDULER.JSP **************************************************
	 */

	@RequestMapping("/scheduler")
	public ModelAndView scheduler(Model model) {
		model.addAttribute("user", new UserModel());

		Map<Integer, String> typeCombo = new HashMap<Integer, String>();

		for (TypeModel t : eventService.getTypesList()) {
			typeCombo.put(t.getId(), t.getName());
		}
		model.addAttribute("typeCombo", typeCombo);
		return new ModelAndView("scheduler", "model", model);
	}

	@RequestMapping(value = "/eventsByDay", method = RequestMethod.GET)
	public @ResponseBody List<Event> getDayEvent(
			@RequestParam("term") String today) {
		System.out.println("Call all day events: " + today);
		List<Event> evList = eventService.getDayEvents(today);
		System.out.println("Get all day events: " + evList.size());
		return evList;
	}

	@RequestMapping(value = "/autocompleteTel", method = RequestMethod.GET)
	public @ResponseBody List<UserModel> getUserByTel(
			@RequestParam("term") String tel) {
		List<UserModel> result = new ArrayList<UserModel>();
		System.out.println("autocomplete user (tel): " + tel);
		for (UserModel u : eventService.getUserList()) {
			if (u.getTel().contains(tel)) {
				System.out.println("found: " + u);
				result.add(u);
			}
		}

		if (result.size() > 15)
			return result.subList(0, 15);
		else
			return result;
	}

	@RequestMapping(value = "/autocompleteMail", method = RequestMethod.GET)
	public @ResponseBody List<UserModel> getUserByMail(
			@RequestParam("term") String tel) {
		List<UserModel> result = new ArrayList<UserModel>();
		System.out.println("autocomplete user (tel): " + tel);
		for (UserModel u : eventService.getUserList()) {
			if (u.getEmail().contains(tel)) {
				System.out.println("found: " + u);
				result.add(u);
			}
		}
		if (result.size() > 15)
			return result.subList(0, 15);
		else
			return result;
	}

	@RequestMapping(value = "/autocompleteName", method = RequestMethod.GET)
	public @ResponseBody List<UserModel> getUserByName(
			@RequestParam("term") String tel) {
		List<UserModel> result = new ArrayList<UserModel>();
		System.out.println("autocomplete user (name): " + tel);
		for (UserModel u : eventService.getUserList()) {
			if (u.getName().toLowerCase().contains(tel.toLowerCase())) {
				System.out.println("found: " + u);
				result.add(u);
			}
		}

		if (result.size() > 15)
			return result.subList(0, 15);
		else
			return result;
	}

	@RequestMapping(value = "/autocompleteSurname", method = RequestMethod.GET)
	public @ResponseBody List<UserModel> getUserBySurname(
			@RequestParam("term") String tel) {
		List<UserModel> result = new ArrayList<UserModel>();
		System.out.println("autocomplete user (surname): " + tel);
		for (UserModel u : eventService.getUserList()) {
			if (u.getSurname() != null
					&& u.getSurname().toLowerCase().contains(tel.toLowerCase())) {
				System.out.println("found: " + u);
				result.add(u);
			}
		}

		if (result.size() > 15)
			return result.subList(0, 15);
		else
			return result;
	}

	@RequestMapping(value = "/addUser", method = RequestMethod.POST)
	public @ResponseBody UserModel addUser(@RequestBody UserModel userModel) {
		System.out.println("User toSave: " + userModel);
		UserModel saved = eventService.saveUser(userModel);
		System.out.println("User Saved: " + saved);
		return saved;

	}

	@RequestMapping(value = "/createUser", method = RequestMethod.POST)
	public ModelAndView createUser(UserModel toAdd, Model model) {
		System.out.println("Create user: " + toAdd);
		UserModel user = eventService.saveUser(toAdd);

		Event ev = new Event();
		ev.setUser(user.getId());
		model.addAttribute("command", ev);
		Map<Integer, String> typeCombo = new HashMap<Integer, String>();
		for (TypeModel t : eventService.getTypesList()) {
			typeCombo.put(t.getId(), t.getName());
		}
		model.addAttribute("user", user);
		model.addAttribute("typeCombo", typeCombo);
		return new ModelAndView("calendar", "model", model);

	}

	@RequestMapping(value = "/addEvent", method = RequestMethod.POST)
	public @ResponseBody String add(@RequestBody Event toAdd) {
		eventService.saveEvent(toAdd);

		System.out.println("Serialization: " + toAdd);
		return "success";

	}

	@RequestMapping(value = "/delEvent", method = RequestMethod.GET)
	public @ResponseBody List<Event> deleteEvent(
			@RequestParam("term") String id, @RequestParam("today") String today) {
		eventService.deleteEventById(id);
		List<Event> evList = eventService.getDayEvents(today);
		System.out.println("Get all day events: " + evList.size());
		return evList;
	}
	
	/*
	 * Cambia lo stato dell'evento da pagato a non pagato e viceversa
	 */
	@RequestMapping(value = "/togglePaidEvent", method = RequestMethod.GET)
	public @ResponseBody List<Event> togglePaidEvent(
			@RequestParam("term") String id, @RequestParam("today") String today) {
		
		Event toUpdate = eventService.getEventById(id);
		if(toUpdate.getPaid()==null ||(toUpdate.getPaid()!=null && "N".equals(toUpdate.getPaid())) ){
			toUpdate.setPaid("Y");
			toUpdate.setPrice( toUpdate.getTypeModel().getSize()
					* toUpdate.getTypeModel().getPrice() 
					* EngineUtils.getDatesDiff( toUpdate.getStartDate(),toUpdate.getEndDate(), TimeUnit.HOURS));
		}else if(toUpdate.getPaid()!=null && "Y".equals(toUpdate.getPaid())){
			toUpdate.setPaid("N");
			toUpdate.setPrice(0);
		}
		eventService.saveEvent(toUpdate);
		List<Event> evList = eventService.getDayEvents(today);
		System.out.println("Get all day events: " + evList.size());
		return evList;
	}
	
	
	 @RequestMapping("/typeCombo")
	 public @ResponseBody List<TypeModel> getTypeList(){
		 log.info("Call TypeList ");
		 return eventService.getTypesList();
	 }
	 

	 @RequestMapping(value = "/loadUser", method = RequestMethod.GET)
	 public @ResponseBody UserModel loadUser(@RequestParam("term") String id) {
		UserModel user = eventService.getUser(Long.parseLong(id));
	 	return user;
	 }

	/*
	 * **************************************************
	 * EVENTS.JSP **************************************************
	 */

	@RequestMapping("/events")
	public ModelAndView showEventsList(Model model) {

		List<Event> evList = eventService.getAllEvents();

		model.addAttribute("eventsList", new ArrayList<Event>());
		model.addAttribute("event", new Event());

		System.out.println("Get all events: " + evList.size());

		return new ModelAndView("events", "model", model);
	}

	@RequestMapping(value = "/eventsSource", method = RequestMethod.GET)
	@ResponseBody
	public DataTableSource getEvents(@RequestParam Map<String, String> map) {

//		List<Event> result = new ArrayList<Event>();
//		int iDisplayStart = Integer.valueOf(map.get("iDisplayStart"));
//		int iDisplayLength = Integer.valueOf(map.get("iDisplayLength"));
//		int pageNumber = (iDisplayStart + iDisplayLength) / iDisplayLength;
		// vedi
		// http://chandrashekhar-dehankar.blogspot.it/2014/10/jquery-datatable-server-side-processing.html
		// vedi anche:
		// http://wangxiangblog.blogspot.it/2013/01/spring-mvc-hibnerate-jquery-datatables.html
		// Chiamata al BE con query parametrizzata secondo i parametri in arrivo
		// dalla dataTable
		for (String key : map.keySet()) {
			System.out.println(key + " : " + map.get(key));
		}
		// Restituisco i risultati come
		DataTableSource source = eventService.getEventList(map);
		System.out.println("RESPONSE: " + source);
		return source;

	}

	@RequestMapping(value = "/saveEvent", method = RequestMethod.POST)
	public @ResponseBody String saveEvent(@RequestBody Event toAdd) {
		System.out.println("Save event: " + toAdd);
		eventService.saveEvent(toAdd);
		return "success";
	}

	/*
	 * **************************************************
	 * TO DELETE DEPRECATED **************************************************
	 */

	// @RequestMapping(value = "/loadEvents", method = RequestMethod.GET)
	// public @ResponseBody List<Event> getEventList() {
	// System.out.println("Get Event List: "+eventService.getAllEvents().size());
	// return eventService.getAllEvents();
	// }



	// @RequestMapping("/calendar")
	// public ModelAndView calendar(Model model) {
	// //return new ModelAndView("calendar");
	//
	// model.addAttribute("command",new Event());
	// Map<Integer,String> typeCombo = new HashMap<Integer, String>();
	//
	// for(TypeModel t: eventService.getTypesList()){
	// typeCombo.put(t.getId(), t.getName());
	// }
	// model.addAttribute("user",new UserModel());
	// model.addAttribute("typeCombo",typeCombo);
	// return new ModelAndView("calendar","model",model);
	// }



	// @RequestMapping(value = "/reservUser/{id}", method = RequestMethod.GET)
	// public ModelAndView reserveUser(@PathVariable Integer id,Model model) {
	// UserModel user = eventService.getUser(id);
	// Event ev = new Event();
	// ev.setUser(user.getId());
	// model.addAttribute("command",ev);
	// Map<Integer,String> typeCombo = new HashMap<Integer, String>();
	// for(TypeModel t: eventService.getTypesList()){
	// typeCombo.put(t.getId(), t.getName());
	// }
	// model.addAttribute("user",user);
	// model.addAttribute("typeCombo",typeCombo);
	// return new ModelAndView("calendar","model",model);
	// }

	// GETTERS AND SETTERS

	public EventService getEventService() {
		return eventService;
	}

	public void setEventService(EventService eventServiceImpl) {
		this.eventService = eventServiceImpl;
	}

}
