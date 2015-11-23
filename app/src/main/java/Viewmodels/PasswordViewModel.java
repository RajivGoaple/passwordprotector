package Viewmodels;

/**
 * Created by prachi on 8/31/2015.
 */
public class PasswordViewModel {
    public  String Id;
    public String Description;
    public String Password;
    public String LastModified;
    public String CategoryId;
    public String Username;
    public PasswordViewModel(String id,String description,String password,String categoryId,String lastModified,String username)
    {
        this.Id=id;
        this.Description=description;
        this.Password=password;
        this.CategoryId=categoryId;
        this.LastModified=lastModified;
        this.Username=username;
    }
}
