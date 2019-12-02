package org.acme.conference.session;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.Optional;

/**
 * SessionResource
 */
@Path("sessions")
@ApplicationScoped
public class SessionResource {

    @Inject
    private SessionStore sessionStore;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Session> allSessions() throws Exception {
        return sessionStore.findAll();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Session createSession(final Session session) throws Exception {
        return sessionStore.save(session);
    }

    @GET
    @Path("/{sessionId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveSession(@PathParam("sessionId") final String sessionId) throws Exception {
        final Optional<Session> result = sessionStore.findById(sessionId);

        return result
            .map(s -> Response.ok(s).build())
            .orElseThrow(NotFoundException::new);
    }

    @PUT
    @Path("/{sessionId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSession(@PathParam("sessionId") final String sessionId, final Session session) throws Exception {
        final Optional<Session> updated = sessionStore.updateById(sessionId, session);
        return updated
            .map(s -> Response.ok(s).build())
            .orElseThrow(NotFoundException::new);
    }

    @DELETE
    @Path("/{sessionId}")
    public Response deleteSession(@PathParam("sessionId") final String sessionId) throws Exception {
        final Optional<Session> removed = sessionStore.deleteById(sessionId);
        return removed
            .map(s-> Response.noContent().build())
            .orElseThrow(NotFoundException::new);
    }

    @GET
    @Path("/{sessionId}/speakers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sessionSpeakers(@PathParam("sessionId") final String sessionId) throws Exception {

        final Optional<Session> session = sessionStore.findById(sessionId);

        return session
            .map(Session::getSpeakers)
            .map(l -> Response.ok(l).build())
            .orElseThrow(NotFoundException::new);
    }

    @PUT
    @Path("/{sessionId}/speakers/{speakerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSessionSpeaker(@PathParam("sessionId") final String sessionId, @PathParam("speakerId") final String speakerId) throws Exception {

        final Optional<Session> result = sessionStore.findById(sessionId);

        if (result.isPresent()) {
            final Session session = result.get();
            final Collection<String> speakers = session.getSpeakers();
            speakers.add(speakerId);
            sessionStore.updateById(sessionId, session);
            return Response.ok(session).build();
        }

        throw new NotFoundException();
    }

    @DELETE
    @Path("/{sessionId}/speakers/{speakerId}")
    public Response removeSessionSpeaker(@PathParam("sessionId") final String sessionId, @PathParam("speakerId") final String speakerId) throws Exception {
        final Optional<Session> result = sessionStore.findById(sessionId);

        if (result.isPresent()) {
            final Session session = result.get();
            final Collection<String> speakers = session.getSpeakers();
            speakers.remove(speakerId);
            sessionStore.updateById(sessionId, session);
            return Response.ok(session).build();
        }

        throw new NotFoundException();
    }
}