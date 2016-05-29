public class DuplicateLoginException extends Exception
{
    public DuplicateLoginException( String username )
    {
        super( "User already logged in: " + username );
    }
}