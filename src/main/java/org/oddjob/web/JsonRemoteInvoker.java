package org.oddjob.web;

import org.oddjob.remote.RemoteException;

/**
 * Something that can invoke operations on a remote Oddjob via JSON.
 */
public interface JsonRemoteInvoker {


    String invoke(String jsonRequest) throws RemoteException;
}
