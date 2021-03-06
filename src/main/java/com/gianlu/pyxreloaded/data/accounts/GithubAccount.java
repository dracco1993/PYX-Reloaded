package com.gianlu.pyxreloaded.data.accounts;

import com.gianlu.pyxreloaded.Consts;
import com.gianlu.pyxreloaded.socials.github.GithubProfileInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

public class GithubAccount extends UserAccount {
    public final String id;

    public GithubAccount(ResultSet user, GithubProfileInfo info) throws SQLException, ParseException {
        super(user, info.emails.isPrimaryEmailVerified());

        id = user.getString("github_user_id");
    }

    public GithubAccount(String nickname, GithubProfileInfo info) {
        super(nickname, info.email, Consts.AuthType.GITHUB, info.emails.isPrimaryEmailVerified(), info.avatarUrl);

        id = info.id;
    }
}
