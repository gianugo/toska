package it.rabellino.toska.gui;

import java.awt.event.ActionListener;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.Composable;

/**
 * @author <a href="gianugo@apache.org">Gianugo Rabellino</a>
 *
 */
public interface Panel 
  extends Component, ActionListener, Composable {
  
  static final String ROLE = "it.rabellino.toska.gui.Panel";

}
