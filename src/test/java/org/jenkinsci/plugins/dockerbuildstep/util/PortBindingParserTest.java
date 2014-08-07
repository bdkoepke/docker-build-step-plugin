package org.jenkinsci.plugins.dockerbuildstep.util;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.dockerjava.client.model.ExposedPort;
import com.github.dockerjava.client.model.Ports;
import com.github.dockerjava.client.model.Ports.Binding;

/**
 *  Defines legal syntax for entering port bindings
 */
public class PortBindingParserTest {
    
    @Test
    public void completeDefinition() throws Exception {
        assertCreatesBinding("8080/tcp 127.0.0.1:80", ExposedPort.tcp(8080), Ports.Binding("127.0.0.1", 80));
    }
    
    @Test
    public void noScheme() throws Exception {
        assertCreatesBinding("8080 127.0.0.1:80", ExposedPort.tcp(8080), Ports.Binding("127.0.0.1", 80));
    }
    
    @Test
    public void noHost() throws Exception {
        assertCreatesBinding("8080/tcp 80", ExposedPort.tcp(8080), Ports.Binding(80));
    }
    
    @Test
    public void minimalDefiniton() throws Exception {
        assertCreatesBinding("8080 80", ExposedPort.tcp(8080), Ports.Binding(80));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void syntaxError() throws Exception {
        PortBindingParser.parseBindings("nonsense");
    }
    
    @Test
    public void twoBindingsUnixStyle() throws Exception {
        twoBindings("8080 80\n8081 81");
    }
    
    @Test
    public void twoBindingsDosStyle() throws Exception {
        twoBindings("8080 80\r\n8081 81");
    }
    
    private void twoBindings(String input) throws Exception {
        Ports ports = PortBindingParser.parseBindings(input);
        assertEquals(2, ports.getBindings().size());
        assertContainsBinding(ports, ExposedPort.tcp(8080), Ports.Binding(80));
        assertContainsBinding(ports, ExposedPort.tcp(8081), Ports.Binding(81));
    }
    
    private static void assertCreatesBinding(String input, ExposedPort exposedPort, Binding expectedBinding) {
        Ports ports = PortBindingParser.parseOneBinding(input);
        assertContainsBinding(ports, exposedPort, expectedBinding);
    }

    private static void assertContainsBinding(Ports ports, ExposedPort exposedPort, Binding expectedBinding) {
        Binding binding = ports.getBindings().get(exposedPort);
        assertNotNull("no binding was created for " + exposedPort, binding);
        assertEquals(expectedBinding, binding);
    }

}