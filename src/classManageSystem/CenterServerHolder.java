package ClassManageSystem;

/**
* ClassManageSystem/CenterServerHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ClassManageSystem.idl
* Saturday, July 29, 2017 10:07:17 AM EDT
*/

public final class CenterServerHolder implements org.omg.CORBA.portable.Streamable
{
  public ClassManageSystem.CenterServer value = null;

  public CenterServerHolder ()
  {
  }

  public CenterServerHolder (ClassManageSystem.CenterServer initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = ClassManageSystem.CenterServerHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    ClassManageSystem.CenterServerHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return ClassManageSystem.CenterServerHelper.type ();
  }

}
