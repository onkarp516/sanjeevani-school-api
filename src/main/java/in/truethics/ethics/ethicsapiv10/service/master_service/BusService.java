package in.truethics.ethics.ethicsapiv10.service.master_service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import in.truethics.ethics.ethicsapiv10.model.school_master.Bus;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.BusRepository;
import in.truethics.ethics.ethicsapiv10.response.ResponseMessage;
import in.truethics.ethics.ethicsapiv10.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BusService {

    private static final Logger busLogger = LoggerFactory.getLogger(BusService.class);


    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    BusRepository busRepository;

    public Object createBus(HttpServletRequest request) {
        ResponseMessage responseObject = new ResponseMessage();

        try {
            Users users = jwtTokenUtil.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            Bus bs = new Bus();
            String checkbus = request.getParameter("busStopName");
            Bus bus1 = busRepository.findByBusStopNameAndStatus(checkbus, true);
            if (bus1 != null) {
                responseObject.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                responseObject.setMessage(" BusStop Name Already Exist !");
                return responseObject;
            } else {
                bs.setBusStopName(checkbus);
                bs.setBusFee(Double.valueOf(request.getParameter("busFee")));
                bs.setCreatedBy(users.getId());
                bs.setStatus(true);
                try {

                    Bus bus = busRepository.save(bs);
                    responseObject.setResponse(bus.getId());
                    responseObject.setMessage("Bus Stop Created Succesfully !");
                    responseObject.setResponseStatus(HttpStatus.OK.value());

                } catch (DataIntegrityViolationException e1) {
                    e1.printStackTrace();
                    busLogger.error("createBusStop -> failed to create BusStop " + e1.getMessage());


                } catch (Exception e) {
                    busLogger.error("createBusStop -> BusStop creation error " + e.getMessage());

                    e.printStackTrace();
                    responseObject.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    responseObject.setMessage("Internal server Error");
                }

            }


        } catch (Exception e) {
            busLogger.error("createBusStop -> BusStop creation error " + e.getMessage());

            e.printStackTrace();
            responseObject.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseObject.setMessage("Internal server Error");
        }
        return responseObject;


    }


    public JsonObject getAllBusStop(HttpServletRequest request) {
        JsonObject res = new JsonObject();
        try {
            Users users = jwtTokenUtil.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            List<Bus> list = busRepository.findByStatusOrderByBusStopNameAsc(true);
            JsonArray result = new JsonArray();
            if (list.size() > 0) {
                for (Bus mGroup : list) {
                    JsonObject response = new JsonObject();
                    response.addProperty("id", mGroup.getId());
                    response.addProperty("busStopName", mGroup.getBusStopName());
                    response.addProperty("busFee", mGroup.getBusFee());
                    result.add(response);
                }
            }
            res.add("responseObject", result);
            res.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e1) {
            busLogger.error("getAllBusStop -> BusStop failed to Load error " + e1.getMessage());
            e1.printStackTrace();
            res.addProperty("message", "Failed to load data");
            res.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return res;
    }


    public ResponseMessage getBusStopById(HttpServletRequest request) {
        ResponseMessage response = new ResponseMessage();
        try {
            Long busStopId = Long.parseLong(request.getParameter("id"));
            Bus bus = busRepository.findByIdAndStatus(busStopId, true);
            if (bus != null) {
                response.setResponse(bus);
                response.setResponseStatus(HttpStatus.OK.value());
            } else {
                response.setMessage("Data not found");
                response.setResponseStatus(HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e1) {
            busLogger.error("getBusStop -> Failed to Load Data " + e1.getMessage());
            e1.printStackTrace();
            response.setMessage("Failed to load data");
            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }


    public Object updateBusStop(HttpServletRequest request) {

        ResponseMessage response = new ResponseMessage();
        try {
            Users users = jwtTokenUtil.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            Bus bs = busRepository.findByIdAndStatus(Long.parseLong(
                    request.getParameter("id")), true);
            if (bs != null) {
                bs.setBusStopName(request.getParameter("busStopName"));
                bs.setBusFee(Double.valueOf(request.getParameter("busFee")));
                bs.setUpdatedBy(users.getId());
                bs.setUpdatedAt(LocalDateTime.now());
                try {
                    busRepository.save(bs);
                    response.setMessage("BusStop updated successfully");
                    response.setResponseStatus(HttpStatus.OK.value());
                } catch (Exception e1) {
                    busLogger.error("updateBusStop -> BusStop update error " + e1.getMessage());
                    e1.printStackTrace();
                    response.setMessage("Failed to update BusStop");
                    response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
            } else {
                response.setMessage("Data not found");
                response.setResponseStatus(HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e1) {
            busLogger.error("UpdateBuStop -> BusStop update error " + e1.getMessage());
            e1.printStackTrace();
            response.setMessage("Failed to update religion");
            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    public JsonObject deleteBus(HttpServletRequest request) {
        JsonObject responseMessage = new JsonObject();
        try {
            Users users = jwtTokenUtil.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            Long id = Long.valueOf(request.getParameter("busId"));
            Bus bus = busRepository.findById(id).get();
            if (bus != null) {
                busRepository.deleteBusByid(id);
                responseMessage.addProperty("responseStatus", HttpStatus.OK.value());
                responseMessage.addProperty(" responseMessage", "Bus Stop Deleted Successfully !");
            } else {
                responseMessage.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
                responseMessage.addProperty("responseMessage", "Failed to Delete Data !");

            }
        } catch (Exception e) {
            e.printStackTrace();
            responseMessage.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseMessage.addProperty("responseMessage", "Failed to Delete Data !");
        }
        return responseMessage;

    }
}
