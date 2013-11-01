package edu.gvsu.cis.cis656.lab3;

import de.uniba.wiai.lspi.chord.service.ServiceException;

public interface PresenceService {
    boolean register(RegistrationInfo reg) throws ServiceException;
    boolean updateRegistrationInformation(RegistrationInfo reg) throws ServiceException;
    void unregister(RegistrationInfo userInfo) throws ServiceException;
    RegistrationInfo lookup(String name) throws ServiceException;
}