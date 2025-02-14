/**
 * Copyright (C) 2014-2019 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.smtp.samples;

/*
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.helger.commons.lang.priviledged.IPrivilegedAction;
import com.sun.mail.smtp.SMTPAddressFailedException;
import com.sun.mail.smtp.SMTPAddressSucceededException;
import com.sun.mail.smtp.SMTPSendFailedException;
import com.sun.mail.smtp.SMTPTransport;

/**
 * Demo app that shows how to construct and send an RFC822 (singlepart) message.
 * allow more than one recipient on the command line This is just a variant of
 * msgsend.java that demonstrates use of some SMTP-specific features.
 *
 * @author Max Spivak
 * @author Bill Shannon
 */
public class MainSMTPSend
{
  /*
   * Example of how to extend the SMTPTransport class. This example illustrates
   * how to issue the XACT command before the SMTPTransport issues the DATA
   * command. public static class SMTPExtension extends SMTPTransport { public
   * SMTPExtension(Session session, URLName url) { super(session, url); // to
   * check that we're being used System.out.println("SMTPExtension: constructed"
   * ); } protected synchronized OutputStream data() throws MessagingException {
   * if (supportsExtension("XACCOUNTING")) issueCommand("XACT", 250); return
   * super.data(); } }
   */

  @SuppressWarnings ("resource")
  public static void main (final String [] argv)
  {
    String to, subject = null, from = null, cc = null, bcc = null, url = null;
    String mailhost = null;
    final String mailer = "smtpsend";
    String file = null;
    String protocol = null, host = null, user = null, password = null;
    String record = null; // name of folder in which to record mail
    boolean debug = false;
    boolean verbose = false;
    boolean auth = false;
    String prot = "smtp";
    final BufferedReader in = new BufferedReader (new InputStreamReader (System.in));
    int optind;

    /*
     * Process command line arguments.
     */
    for (optind = 0; optind < argv.length; optind++)
    {
      if (argv[optind].equals ("-T"))
      {
        protocol = argv[++optind];
      }
      else
        if (argv[optind].equals ("-H"))
        {
          host = argv[++optind];
        }
        else
          if (argv[optind].equals ("-U"))
          {
            user = argv[++optind];
          }
          else
            if (argv[optind].equals ("-P"))
            {
              password = argv[++optind];
            }
            else
              if (argv[optind].equals ("-M"))
              {
                mailhost = argv[++optind];
              }
              else
                if (argv[optind].equals ("-f"))
                {
                  record = argv[++optind];
                }
                else
                  if (argv[optind].equals ("-a"))
                  {
                    file = argv[++optind];
                  }
                  else
                    if (argv[optind].equals ("-s"))
                    {
                      subject = argv[++optind];
                    }
                    else
                      if (argv[optind].equals ("-o"))
                      { // originator
                        from = argv[++optind];
                      }
                      else
                        if (argv[optind].equals ("-c"))
                        {
                          cc = argv[++optind];
                        }
                        else
                          if (argv[optind].equals ("-b"))
                          {
                            bcc = argv[++optind];
                          }
                          else
                            if (argv[optind].equals ("-L"))
                            {
                              url = argv[++optind];
                            }
                            else
                              if (argv[optind].equals ("-d"))
                              {
                                debug = true;
                              }
                              else
                                if (argv[optind].equals ("-v"))
                                {
                                  verbose = true;
                                }
                                else
                                  if (argv[optind].equals ("-A"))
                                  {
                                    auth = true;
                                  }
                                  else
                                    if (argv[optind].equals ("-S"))
                                    {
                                      prot = "smtps";
                                    }
                                    else
                                      if (argv[optind].equals ("--"))
                                      {
                                        optind++;
                                        break;
                                      }
                                      else
                                        if (argv[optind].startsWith ("-"))
                                        {
                                          System.out.println ("Usage: smtpsend [[-L store-url] | [-T prot] [-H host] [-U user] [-P passwd]]");
                                          System.out.println ("\t[-s subject] [-o from-address] [-c cc-addresses] [-b bcc-addresses]");
                                          System.out.println ("\t[-f record-mailbox] [-M transport-host] [-d] [-a attach-file]");
                                          System.out.println ("\t[-v] [-A] [-S] [address]");
                                          System.exit (1);
                                        }
                                        else
                                        {
                                          break;
                                        }
    }

    try
    {
      /*
       * Prompt for To and Subject, if not specified.
       */
      if (optind < argv.length)
      {
        // - concatenate all remaining arguments
        to = argv[optind];
        System.out.println ("To: " + to);
      }
      else
      {
        System.out.print ("To: ");
        System.out.flush ();
        to = in.readLine ();
      }
      if (subject == null)
      {
        System.out.print ("Subject: ");
        System.out.flush ();
        subject = in.readLine ();
      }
      else
      {
        System.out.println ("Subject: " + subject);
      }

      /*
       * Initialize the JavaMail Session.
       */
      final Properties props = IPrivilegedAction.systemGetProperties ().invokeSafe ();
      if (mailhost != null)
        props.put ("mail." + prot + ".host", mailhost);
      if (auth)
        props.put ("mail." + prot + ".auth", "true");

      /*
       * Create a Provider representing our extended SMTP transport and set the
       * property to use our provider. Provider p = new
       * Provider(Provider.Type.TRANSPORT, prot, "smtpsend$SMTPExtension",
       * "JavaMail demo", "no version"); props.put("mail." + prot + ".class",
       * "smtpsend$SMTPExtension");
       */

      // Get a Session object
      final Session session = Session.getInstance (props, null);
      if (debug)
        session.setDebug (true);

      /*
       * Register our extended SMTP transport. session.addProvider(p);
       */

      /*
       * Construct the message and send it.
       */
      final Message msg = new MimeMessage (session);
      if (from != null)
        msg.setFrom (new InternetAddress (from));
      else
        msg.setFrom ();

      msg.setRecipients (Message.RecipientType.TO, InternetAddress.parse (to, false));
      if (cc != null)
        msg.setRecipients (Message.RecipientType.CC, InternetAddress.parse (cc, false));
      if (bcc != null)
        msg.setRecipients (Message.RecipientType.BCC, InternetAddress.parse (bcc, false));

      msg.setSubject (subject);

      final String text = collect (in);

      if (file != null)
      {
        // Attach the specified file.
        // We need a multipart message to hold the attachment.
        final MimeBodyPart mbp1 = new MimeBodyPart ();
        mbp1.setText (text);
        final MimeBodyPart mbp2 = new MimeBodyPart ();
        mbp2.attachFile (file);
        final MimeMultipart mp = new MimeMultipart ();
        mp.addBodyPart (mbp1);
        mp.addBodyPart (mbp2);
        msg.setContent (mp);
      }
      else
      {
        // If the desired charset is known, you can use
        // setText(text, charset)
        msg.setText (text);
      }

      msg.setHeader ("X-Mailer", mailer);
      msg.setSentDate (new Date ());

      // send the thing off
      /*
       * The simple way to send a message is this: Transport.send(msg); But
       * we're going to use some SMTP-specific features for demonstration
       * purposes so we need to manage the Transport object explicitly.
       */
      try (final SMTPTransport t = (SMTPTransport) session.getTransport (prot))
      {
        if (auth)
          t.connect (mailhost, user, password);
        else
          t.connect ();
        t.sendMessage (msg, msg.getAllRecipients ());
        if (verbose)
          System.out.println ("Response: " + t.getLastServerResponse ());
      }

      System.out.println ("\nMail was sent successfully.");

      /*
       * Save a copy of the message, if requested.
       */
      if (record != null)
      {
        // Get a Store object
        Store store = null;
        if (url != null)
        {
          final URLName urln = new URLName (url);
          store = session.getStore (urln);
          store.connect ();
        }
        else
        {
          if (protocol != null)
            store = session.getStore (protocol);
          else
            store = session.getStore ();

          // Connect
          if (host != null || user != null || password != null)
            store.connect (host, user, password);
          else
            store.connect ();
        }

        // Get record Folder. Create if it does not exist.
        final Folder folder = store.getFolder (record);
        if (folder == null)
        {
          System.err.println ("Can't get record folder.");
          System.exit (1);
        }
        if (!folder.exists ())
          folder.create (Folder.HOLDS_MESSAGES);

        final Message [] msgs = new Message [1];
        msgs[0] = msg;
        folder.appendMessages (msgs);

        System.out.println ("Mail was recorded successfully.");
      }

    }
    catch (final MessagingException | IOException e)
    {
      /*
       * Handle SMTP-specific exceptions.
       */
      if (e instanceof SendFailedException)
      {
        MessagingException sfe = (MessagingException) e;
        if (sfe instanceof SMTPSendFailedException)
        {
          final SMTPSendFailedException ssfe = (SMTPSendFailedException) sfe;
          System.out.println ("SMTP SEND FAILED:");
          if (verbose)
            System.out.println (ssfe.toString ());
          System.out.println ("  Command: " + ssfe.getCommand ());
          System.out.println ("  RetCode: " + ssfe.getReturnCode ());
          System.out.println ("  Response: " + ssfe.getMessage ());
        }
        else
        {
          if (verbose)
            System.out.println ("Send failed: " + sfe.toString ());
        }
        Exception ne;
        while ((ne = sfe.getNextException ()) != null && ne instanceof MessagingException)
        {
          sfe = (MessagingException) ne;
          if (sfe instanceof SMTPAddressFailedException)
          {
            final SMTPAddressFailedException ssfe = (SMTPAddressFailedException) sfe;
            System.out.println ("ADDRESS FAILED:");
            if (verbose)
              System.out.println (ssfe.toString ());
            System.out.println ("  Address: " + ssfe.getAddress ());
            System.out.println ("  Command: " + ssfe.getCommand ());
            System.out.println ("  RetCode: " + ssfe.getReturnCode ());
            System.out.println ("  Response: " + ssfe.getMessage ());
          }
          else
            if (sfe instanceof SMTPAddressSucceededException)
            {
              System.out.println ("ADDRESS SUCCEEDED:");
              final SMTPAddressSucceededException ssfe = (SMTPAddressSucceededException) sfe;
              if (verbose)
                System.out.println (ssfe.toString ());
              System.out.println ("  Address: " + ssfe.getAddress ());
              System.out.println ("  Command: " + ssfe.getCommand ());
              System.out.println ("  RetCode: " + ssfe.getReturnCode ());
              System.out.println ("  Response: " + ssfe.getMessage ());
            }
        }
      }
      else
      {
        System.out.println ("Got Exception: " + e);
        if (verbose)
          e.printStackTrace ();
      }
    }
  }

  /**
   * Read the body of the message until EOF.
   *
   * @param in
   *        reader
   * @return All lines
   * @throws IOException
   *         in case of error
   */
  public static String collect (final BufferedReader in) throws IOException
  {
    String line;
    final StringBuffer sb = new StringBuffer ();
    while ((line = in.readLine ()) != null)
    {
      sb.append (line);
      sb.append ("\n");
    }
    return sb.toString ();
  }
}
