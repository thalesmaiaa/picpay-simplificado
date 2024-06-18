package com.example.picpay.controllers;



import static org.springframework.http.ResponseEntity.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


@Component
public class AbstractController
{
    @Autowired protected ObjectMapper jsonMapper;

/* --------------------------------------------------------------------------------------------- */

    protected ObjectNode json() {
        return jsonMapper.createObjectNode();
    }

/* --------------------------------------------------------------------------------------------- */

    protected ObjectNode success() {
        return json().put("status", "success");
    }

/* --------------------------------------------------------------------------------------------- */

    protected ObjectNode failed() {
        return json().put("status", "failed");
    }

/* --------------------------------------------------------------------------------------------- */

    protected ResponseEntity<?> validationFailed(ObjectNode error) {
        return ok(failed().set("rejected", error));
    }

/* --------------------------------------------------------------------------------------------- */

    protected ObjectNode error(String description) {
        return json().put("error", description);
    }

/* --------------------------------------------------------------------------------------------- */


    protected ObjectNode error(ObjectNode error) {
        return json().putPOJO("error", error);
    }

/* --------------------------------------------------------------------------------------------- */


    protected ResponseEntity<?> ok() {
        return status(HttpStatus.OK).build();
    }

/* --------------------------------------------------------------------------------------------- */

    protected ResponseEntity<?> ok(Object body) {
        return status(HttpStatus.OK).body(body);
    }

/* --------------------------------------------------------------------------------------------- */

    protected ResponseEntity<?> created(Object id)
    {
        ObjectNode body = success();

        if (id != null) {
            body.put("id", id.toString());
        }

        return status(HttpStatus.CREATED).body(body);
    }

    protected Boolean isErrorResponse(ObjectNode response){
        return response.get("status_code").asText().contains("40");
    }

/* --------------------------------------------------------------------------------------------- */

    protected ResponseEntity<ObjectNode> badRequest(String description) {
        return status(HttpStatus.BAD_REQUEST).body(error(description));
    }

/* --------------------------------------------------------------------------------------------- */
    protected ResponseEntity<ObjectNode> badRequest(ObjectNode error) {
        return status(HttpStatus.BAD_REQUEST).body(error(error));
    }

/* --------------------------------------------------------------------------------------------- */

    protected ResponseEntity<ObjectNode> unauthorized(ObjectNode description) {
        return status(HttpStatus.UNAUTHORIZED).body(error(description));
    }

/* --------------------------------------------------------------------------------------------- */

    protected ResponseEntity<ObjectNode> forbidden(String description) {
        return status(HttpStatus.FORBIDDEN).body(error(description));
    }



/* --------------------------------------------------------------------------------------------- */

    protected ResponseEntity<ObjectNode> notFound(String description) {
        return status(HttpStatus.NOT_FOUND).body(error(description));
    }

/* --------------------------------------------------------------------------------------------- */

    protected ResponseEntity<?> serverError() {
        return status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


}
