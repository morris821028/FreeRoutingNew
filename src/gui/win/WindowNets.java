/*
 *  Copyright (C) 2014  Alfons Wirtz  
 *   website www.freerouting.net
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License at <http://www.gnu.org/licenses/> 
 *   for more details.
 *
 * NetsWindow.java
 *
 * Created on 24. Maerz 2005, 07:41
 */

package gui.win;

import freert.rules.NetClass;
import freert.rules.NetClasses;
import freert.rules.RuleNet;
import freert.rules.RuleNets;
import gui.BoardFrame;

/**
 *
 * @author Alfons Wirtz
 */
public class WindowNets extends WindowObjectListWithFilter
   {
   private static final long serialVersionUID = 1L;

   public WindowNets(BoardFrame p_board_frame)
      {
      super(p_board_frame);
      this.resources = java.util.ResourceBundle.getBundle("gui.resources.WindowNets", p_board_frame.get_locale());
      this.setTitle(resources.getString("title"));

      javax.swing.JPanel curr_button_panel = new javax.swing.JPanel();
      this.south_panel.add(curr_button_panel, java.awt.BorderLayout.NORTH);

      final javax.swing.JButton assign_class_button = new javax.swing.JButton(resources.getString("assign_class"));
      curr_button_panel.add(assign_class_button);
      assign_class_button.setToolTipText(resources.getString("assign_class_tooltip"));
      assign_class_button.addActionListener(new AssignClassListener());

      final javax.swing.JButton filter_incompletes_button = new javax.swing.JButton(resources.getString("filter_incompletes"));
      curr_button_panel.add(filter_incompletes_button);
      filter_incompletes_button.setToolTipText(resources.getString("filter_incompletes_tooltip"));
      filter_incompletes_button.addActionListener(new FilterIncompletesListener());
      p_board_frame.set_context_sensitive_help(this, "WindowObjectList_Nets");
      }

   /**
    * Fills the list with the nets in the net list.
    */
   protected void fill_list()
      {
      RuleNets nets = this.board_frame.board_panel.itera_board.get_routing_board().brd_rules.nets;
      RuleNet[] sorted_arr = new RuleNet[nets.max_net_no()];
      for (int i = 0; i < sorted_arr.length; ++i)
         {
         sorted_arr[i] = nets.get(i + 1);
         }
      java.util.Arrays.sort(sorted_arr);
      for (int i = 0; i < sorted_arr.length; ++i)
         {
         this.add_to_list(sorted_arr[i]);
         }
      this.gui_list.setVisibleRowCount(Math.min(sorted_arr.length, DEFAULT_TABLE_SIZE));
      }

   protected void select_instances()
      {
      @SuppressWarnings("deprecation")
      Object[] selected_nets = gui_list.getSelectedValues();
      if (selected_nets.length <= 0)
         {
         return;
         }
      int[] selected_net_numbers = new int[selected_nets.length];
      for (int i = 0; i < selected_nets.length; ++i)
         {
         selected_net_numbers[i] = ((RuleNet) selected_nets[i]).net_number;
         }
      board.RoutingBoard routing_board = board_frame.board_panel.itera_board.get_routing_board();
      java.util.Set<board.items.BrdItem> selected_items = new java.util.TreeSet<board.items.BrdItem>();
      java.util.Collection<board.items.BrdItem> board_items = routing_board.get_items();
      for (board.items.BrdItem curr_item : board_items)
         {
         boolean item_matches = false;
         for (int curr_net_no : selected_net_numbers)
            {
            if (curr_item.contains_net(curr_net_no))
               {
               item_matches = true;
               break;
               }
            }
         if (item_matches)
            {
            selected_items.add(curr_item);
            }
         }
      board_frame.board_panel.itera_board.select_items(selected_items);
      board_frame.board_panel.itera_board.zoom_selection();
      }

   private final java.util.ResourceBundle resources;

   private class AssignClassListener implements java.awt.event.ActionListener
      {
      public void actionPerformed(java.awt.event.ActionEvent p_evt)
         {
         @SuppressWarnings("deprecation")
         Object[] selected_nets = gui_list.getSelectedValues();
         if (selected_nets.length <= 0)
            {
            return;
            }
         NetClasses net_classes = board_frame.board_panel.itera_board.get_routing_board().brd_rules.net_classes;
         NetClass[] class_arr = new NetClass[net_classes.count()];
         for (int i = 0; i < class_arr.length; ++i)
            {
            class_arr[i] = net_classes.get(i);
            }
         
         Object selected_value = javax.swing.JOptionPane.showInputDialog(null, resources.getString("message_1"), resources.getString("message_2"), javax.swing.JOptionPane.INFORMATION_MESSAGE, null,
               class_arr, class_arr[0]);
         if (!(selected_value instanceof NetClass))
            {
            return;
            }
         NetClass selected_class = (NetClass) selected_value;
         for (int i = 0; i < selected_nets.length; ++i)
            {
            ((RuleNet) selected_nets[i]).set_class(selected_class);
            }
         board_frame.refresh_windows();
         }
      }

   private class FilterIncompletesListener implements java.awt.event.ActionListener
      {
      public void actionPerformed(java.awt.event.ActionEvent p_evt)
         {
         @SuppressWarnings("deprecation")
         Object[] selected_nets = gui_list.getSelectedValues();
         if (selected_nets.length <= 0)
            {
            return;
            }
         interactive.IteraBoard board_handling = board_frame.board_panel.itera_board;
         int max_net_no = board_handling.get_routing_board().brd_rules.nets.max_net_no();
         for (int i = 1; i <= max_net_no; ++i)
            {
            board_handling.set_incompletes_filter(i, true);
            }
         for (int i = 0; i < selected_nets.length; ++i)
            {
            board_handling.set_incompletes_filter(((RuleNet) selected_nets[i]).net_number, false);
            }
         board_frame.board_panel.repaint();
         }
      }
   }
