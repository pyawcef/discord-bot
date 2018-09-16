package skillcoin_bot;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.client.events.group.GroupUserJoinEvent;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class SkillcoinBot extends ListenerAdapter {

	public static void main(String[] args) {
		try {
			SkillcoinBot skillcoinBot = new SkillcoinBot();
			System.out.println("START");
			JDA jda = new JDABuilder(AccountType.BOT)
					.setToken(Env.ENV_TOKEN)
					.addEventListener(skillcoinBot).buildAsync();
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}

	boolean nighmodeActivated = false;
	
	@Override
	public void onGenericEvent(Event event) {
		super.onGenericEvent(event);
		System.out.println("EVENT");
	}
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		super.onGuildMemberJoin(event);
		
		Guild guild = event.getGuild();
		Member member = guild.getMember(event.getUser());
		for (Role role : event.getMember().getRoles()) {
			if (Env.ENV_ROLE_ID.equals(role.getId())) {
				guild.getController().removeRolesFromMember(member, role).complete();
			}
		}
	}
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		
		User user = event.getAuthor();
		String message = event.getMessage().getContentDisplay();
		MessageChannel channel = event.getChannel();
		System.out.println("id :" + user.getId());
		if ("!nightmode".equals(message)) {
			if (Env.ENV_USER_ID.equals(user.getId())) {
				nighmodeActivated = true;
				event.getMessage().delete().complete();
				channel.sendMessage("nightmode aktiviert. Alle Team-Mitglieder kriegen ihre Rechte entzogen")
						.complete();
			} else {
				channel.sendMessage(user.getName() + "!! Du bist kein Admin! Lass es doch einfach!").complete();
			}
		}

		if (!nighmodeActivated) {
			System.out.println("nightmode deaktiviert!");
			return;
		}

		System.out.println(
				"we have a message " + event.getAuthor().getName() + " : " + event.getMessage().getContentDisplay());
		System.out.println("roles: " + event.getMember().getRoles());


		Guild guild = event.getGuild();
		Member member = guild.getMember(event.getAuthor());
		for (Role role : event.getMember().getRoles()) {
			if ("OWNER".equals(role.getName())) {
				guild.getController().removeRolesFromMember(member, role).complete();
			}
		}

		System.out.println("member " + event.getMember());
		System.out.println("permissions: " + event.getMember().getPermissions());
		System.out.println("roles: " + event.getMember().getRoles());

	}

}
